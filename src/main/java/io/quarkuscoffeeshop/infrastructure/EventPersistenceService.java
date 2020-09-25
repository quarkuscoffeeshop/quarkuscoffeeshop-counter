package io.quarkuscoffeeshop.infrastructure;

import io.quarkuscoffeeshop.counter.domain.CoffeeshopEvent;
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
import javax.transaction.Transactional;
import java.io.StringReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class EventPersistenceService {

    Logger logger = LoggerFactory.getLogger(EventPersistenceService.class);

    @Inject
    CoffeeshopEventRepository coffeeshopEventRepository;

    @Incoming("order-events") @Transactional
    public CompletionStage<Void> recordOrderEvent(final Message message) {
        logger.debug("received {}", message.getPayload());
        String payload = (String) message.getPayload();
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject jsonObject = jsonReader.readObject();
        logger.debug("unmarshalled {}", jsonObject);
        EventType eventType = EventType.valueOf(jsonObject.getString("eventType"));
        CoffeeshopEvent coffeeshopEvent = new CoffeeshopEvent(eventType, payload);
        logger.debug("CoffeeshopEvent {}", coffeeshopEvent);
        coffeeshopEventRepository.persist(coffeeshopEvent);
        return message.ack();
    }
}
