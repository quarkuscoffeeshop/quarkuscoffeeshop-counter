package io.quarkuscoffeeshop.infrastructure;

import io.quarkuscoffeeshop.counter.domain.CoffeeshopCommand;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopEvent;
import io.quarkuscoffeeshop.domain.CommandType;
import io.quarkuscoffeeshop.domain.EventType;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EventPersistenceService {

    Logger logger = LoggerFactory.getLogger(EventPersistenceService.class);

    @Inject
    CoffeeshopEventRepository coffeeshopEventRepository;

    @Inject
    CoffeeShopCommandRepository coffeeShopCommandRepository;

    @Incoming("order-events")
    public CompletionStage<Void> recordOrderEvent(final String message) {

        return CompletableFuture.runAsync(() -> {
            logger.debug("recordOrderEvent {}", message);
            JsonReader jsonReader = Json.createReader(new StringReader(message));
            JsonObject jsonObject = jsonReader.readObject();
            if (jsonObject.containsKey("eventType")){
                CoffeeshopEvent coffeeshopEvent = new CoffeeshopEvent(
                        EventType.valueOf(jsonObject.getString("eventType")), message);
                logger.debug("CoffeeshopEvent {}", coffeeshopEvent);
                coffeeshopEventRepository.persist(coffeeshopEvent);
            }else if(jsonObject.containsKey("commandType")){
                CoffeeshopCommand coffeeshopCommand = new CoffeeshopCommand(
                        CommandType.valueOf(jsonObject.getString("commandType")), message);
                logger.debug("CoffeeshopCommand {}", coffeeshopCommand);
                coffeeShopCommandRepository.persist(coffeeshopCommand);
            }
        });
    }
}
