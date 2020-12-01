package io.quarkuscoffeeshop.counter.domain.events;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;

import java.time.Instant;

public class OrderPlacedEvent implements ExportedEvent<String, JsonNode>, CoffeeshopEvent {

    private static final String TYPE = "Order";
    private static final String EVENT_TYPE = "OrderCreated";

    private final String orderId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    public OrderPlacedEvent(String orderId, JsonNode jsonNode, Instant timestamp) {
        this.orderId = orderId;
        this.jsonNode = jsonNode;
        this.timestamp = timestamp;
    }

    @Override
    public String getAggregateId() {
        return orderId;
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
