package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@RegisterForReflection
public class OrderCreatedEvent {

    public Order order;

    public List<LineItemEvent> events = new ArrayList<>();

    public void addEvent(LineItemEvent orderEvent) {
        getEvents().add(orderEvent);
    }

    public List<LineItemEvent> getEvents() {
        if (this.events == null) {
            this.events = new ArrayList<>();
        }
        return this.events;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void addEvents(List<LineItemEvent> orderEvents) {
        getEvents().addAll(orderEvents);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderCreatedEvent.class.getSimpleName() + "[", "]")
                .add("order=" + order)
                .add("events=" + events)
                .toString();
    }

    @Override
    public boolean equals(Object o) {


        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(order, that.order) &&
                Objects.equals(events, that.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, events);
    }
}
