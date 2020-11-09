package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkuscoffeeshop.domain.EventType;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Objects;
import java.util.StringJoiner;

@RegisterForReflection
@Entity
public class OrderEvent extends PanacheEntity {

    EventType eventType;

    @Lob
    String eventPayload;

    public OrderEvent() {
    }

    public OrderEvent(EventType eventType, String eventPayload) {
        this.id = id;
        this.eventPayload = eventPayload;
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderEvent.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("eventType=" + eventType)
                .add("eventPayload='" + eventPayload + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEvent that = (OrderEvent) o;
        return eventType == that.eventType &&
                Objects.equals(eventPayload, that.eventPayload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, eventPayload);
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }
}
