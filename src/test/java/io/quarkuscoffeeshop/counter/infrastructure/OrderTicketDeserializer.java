package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderTicket;

public class OrderTicketDeserializer  extends ObjectMapperDeserializer<OrderTicket> {

    public OrderTicketDeserializer() {
        super(OrderTicket.class);
    }
}
