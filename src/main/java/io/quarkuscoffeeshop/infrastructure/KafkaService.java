package io.quarkuscoffeeshop.infrastructure;

import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.counter.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.context.ManagedExecutor;
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
    ManagedExecutor managedExecutor;

    @Inject
    OrderRepository orderRepository;

    @Inject
    ReceiptRepository receiptRepository;

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
    public CompletionStage<Void> onOrderIn(final String message) {
        logger.debug("message received from orders topic: {}", message);

        JsonReader jsonReader = Json.createReader(new StringReader(message));
        JsonObject jsonObject = jsonReader.readObject();

        if(jsonObject.containsKey("commandType")){
            logger.info("{} received from orders topic", jsonObject.getString("commandType"));
            final PlaceOrderCommand placeOrderCommand = JsonUtil.createPlaceOrderCommandFromJson(message);
            return handlePlaceOrderCommand(JsonUtil.createPlaceOrderCommandFromJson(message));
        }else if (jsonObject.containsKey("eventType")) {
            String eventType = jsonObject.getString("eventType");
            logger.info("event received from orders topic: {}", eventType);
            switch (eventType) {
                case "BEVERAGE_ORDER_UP":
                    return webUpdatesOutEmitter.send(JsonUtil.toDashboardUpdateReadyJson(message));
                case "KITCHEN_ORDER_UP":
                    return webUpdatesOutEmitter.send(JsonUtil.toDashboardUpdateReadyJson(message));
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    CompletionStage<Void> sendBaristaOrder(final LineItemEvent event) {
        logger.debug("sendBaristaOrder: {}", JsonUtil.toJson(event));
        return baristaOutEmitter.send(JsonUtil.toJson(event)).thenRun(() ->{
            logger.debug("sending web update {}", event);
            sendInProgressWebUpdate(event);
        }).exceptionally(ex -> {
            //TODO: Send web error message
            sendInProgressWebUpdate(event);
            return null;
        });
    }

    CompletableFuture<Void> sendKitchenOrder(final LineItemEvent event) {
        logger.debug("sendKitchenOrder: {}", JsonUtil.toJson(event));
        return kitchenOutEmitter.send(JsonUtil.toJson(event)).thenRun(() ->{
            logger.debug("sending web update {}", event);
            sendInProgressWebUpdate(event);
        }).toCompletableFuture();
    }

    CompletableFuture<Void> sendInProgressWebUpdate(final LineItemEvent event) {
        logger.debug("sendWebUpdate: {}", JsonUtil.toInProgressUpdate(event));
        return webUpdatesOutEmitter.send(JsonUtil.toInProgressUpdate(event)).toCompletableFuture();
    }

    CompletableFuture<Void> persistOrder(Order order) {

        return CompletableFuture.runAsync(() -> {
            orderRepository.persist(order);
            logger.debug("persisted {}", order);
        }).exceptionally(e -> { logger.error(e.getMessage()); return null; }).toCompletableFuture();
    }

    CompletionStage<Void> persistReceipt(Receipt receipt) {
        return CompletableFuture.runAsync(() -> {
            receiptRepository.persist(receipt);
            logger.debug("persisted {}", receipt);
        }).exceptionally(ex -> {
            logger.error("error persisting {}: {}", receipt, ex.getMessage());
            return null;
        });
    }


    protected CompletionStage<Void> handlePlaceOrderCommand(final PlaceOrderCommand placeOrderCommand) {

        logger.debug("PlaceOrderCommand received: {}", placeOrderCommand);
        // Get the event from the Order domain object
        OrderCreatedEvent orderCreatedEvent = Order.handlePlaceOrderCommand(placeOrderCommand);
//        orderRepository.persist(orderCreatedEvent.order);

//        receiptRepository.persist(orderCreatedEvent.getReceipt());
        managedExecutor.submit(() ->{
            logger.debug("persisting {}", orderCreatedEvent.getReceipt());
            receiptRepository.persist(orderCreatedEvent.getReceipt());
            logger.debug("persisted {}", orderCreatedEvent.getReceipt());
        });

        Collection<CompletableFuture<Void>> futures = new ArrayList<>((orderCreatedEvent.getEvents().size() * 2) + 1);
//        futures.add(persistOrder(orderCreatedEvent.order));
//        futures.add(persistReceipt(orderCreatedEvent.getReceipt()));
        orderCreatedEvent.getEvents().forEach(e ->{
            if (e.eventType.equals(EventType.BEVERAGE_ORDER_IN)) {
                futures.add(sendBaristaOrder(e).toCompletableFuture());
            } else if (e.eventType.equals(EventType.KITCHEN_ORDER_IN)) {
                //TODO: change
                futures.add(sendKitchenOrder(e));
            }
        });

        //TODO: change so that successful messages complete
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .exceptionally(e -> {
                    logger.error(e.getMessage());
                    return null;
                });
    }

}