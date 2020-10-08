package io.quarkuscoffeeshop.infrastructure;

import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.counter.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped @RegisterForReflection
public class KafkaService {

    final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    OrderRepository orderRepository;

    @Inject
    @Channel("barista-out")
    Emitter<String> baristaOutEmitter;

    @Inject
    @Channel("kitchen-out")
    Emitter<String> kitchenOutEmitter;

    @Inject
    @Channel("web-updates-out")
    Emitter<String> webUpdatesOutEmitter;

    @Incoming("orders")
    public CompletionStage<Void> onOrderIn(final Message message) {
        String payload = (String) message.getPayload();
        logger.debug("message received from orders topic: {}", payload);

        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject jsonObject = jsonReader.readObject();

        if(jsonObject.containsKey("commandType")){
            logger.info("command received from orders topic: {}", jsonObject.getString("commandType"));
            final PlaceOrderCommand placeOrderCommand = JsonUtil.createPlaceOrderCommandFromJson(payload);
            return handlePlaceOrderCommand(JsonUtil.createPlaceOrderCommandFromJson(message.getPayload().toString())).thenRun(()->{message.ack();});
        }else if (jsonObject.containsKey("eventType")) {
            String eventType = jsonObject.getString("eventType");
            logger.info("event received from orders topic: {}", eventType);
            switch (eventType) {
                case "BEVERAGE_ORDER_IN":
                    return message.ack();
                case "KITCHEN_ORDER_IN":
                    return message.ack();
                case "BEVERAGE_ORDER_UP":
                    return webUpdatesOutEmitter.send(JsonUtil.toDashboardUpdateReadyJson(payload)).toCompletableFuture().thenRun(()->{message.ack();});
                case "KITCHEN_ORDER_UP":
                    return webUpdatesOutEmitter.send(JsonUtil.toDashboardUpdateReadyJson(payload)).toCompletableFuture().thenRun(()->{message.ack();});
                default:
                    return null;
            }
        }
        return null;
    }

    CompletableFuture<Void> sendBaristaOrder(final LineItemEvent event) {
        logger.debug("sendBaristaOrder: {}", JsonUtil.toJson(event));
        return baristaOutEmitter.send(JsonUtil.toJson(event)).thenRun(() ->{
            sendInProgressWebUpdate(event);
        }).toCompletableFuture().toCompletableFuture();
    }

    CompletableFuture<Void> sendKitchenOrder(final LineItemEvent event) {
        logger.debug("sendKitchenOrder: {}", JsonUtil.toJson(event));
        return kitchenOutEmitter.send(JsonUtil.toJson(event)).thenRun(() ->{
            sendInProgressWebUpdate(event);
        }).toCompletableFuture();
    }

    CompletableFuture<Void> sendInProgressWebUpdate(final LineItemEvent event) {
        logger.debug("sendWebUpdate: {}", JsonUtil.toInProgressUpdate(event));
        return webUpdatesOutEmitter.send(JsonUtil.toInProgressUpdate(event)).toCompletableFuture();
    }

    protected CompletionStage<Void> handlePlaceOrderCommand(final PlaceOrderCommand placeOrderCommand) {

        logger.debug("PlaceOrderCommand received: {}", placeOrderCommand);
        // Get the event from the Order domain object
        OrderCreatedEvent orderCreatedEvent = Order.handlePlaceOrderCommand(placeOrderCommand);
        orderRepository.persist(orderCreatedEvent.order);

        Collection<CompletableFuture<Void>> futures = new ArrayList<>(orderCreatedEvent.getEvents().size() * 2);
        orderCreatedEvent.getEvents().forEach(e ->{
            if (e.eventType.equals(EventType.BEVERAGE_ORDER_IN)) {
                futures.add(sendBaristaOrder(e));
            } else if (e.eventType.equals(EventType.KITCHEN_ORDER_IN)) {
                futures.add(sendKitchenOrder(e));
            }
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .exceptionally(e -> {
                    logger.error(e.getMessage());
                    return null;
                });
    }

}