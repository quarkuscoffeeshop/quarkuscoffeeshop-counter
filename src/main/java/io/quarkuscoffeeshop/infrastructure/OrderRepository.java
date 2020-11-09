package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkuscoffeeshop.counter.domain.Order;
import io.quarkuscoffeeshop.counter.domain.OrderCreatedEvent;
import io.quarkuscoffeeshop.counter.domain.OrderEvent;
import io.quarkuscoffeeshop.domain.EventType;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;

@ApplicationScoped
public class OrderRepository implements PanacheMongoRepository<OrderEvent> {

    public void persist(final OrderCreatedEvent orderCreatedEvent) {
        OrderEvent orderEvent = new OrderEvent(
                EventType.ORDER_PLACED,
                JsonUtil.toJson(orderCreatedEvent));
        orderEvent.persist();
    }
}
