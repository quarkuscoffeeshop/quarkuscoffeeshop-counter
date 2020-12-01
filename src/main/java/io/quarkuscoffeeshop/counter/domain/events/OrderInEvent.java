package io.quarkuscoffeeshop.counter.domain.events;

import io.quarkuscoffeeshop.counter.domain.Item;

import java.time.Instant;
import java.util.StringJoiner;

public class OrderInEvent implements CoffeeshopEvent{

    EventType eventType;
    String orderId;
    String id;
    String name;
    Item item;
    Instant timestamp;

    public OrderInEvent() {
    }

    public OrderInEvent(EventType eventType, String orderId, String id, String name, Item item, Instant timestamp) {
        this.eventType = eventType;
        this.orderId = orderId;
        this.id = id;
        this.name = name;
        this.item = item;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderInEvent.class.getSimpleName() + "[", "]")
                .add("eventType=" + eventType)
                .add("orderId='" + orderId + "'")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("item=" + item)
                .add("timestamp=" + timestamp)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderInEvent that = (OrderInEvent) o;

        if (eventType != that.eventType) return false;
        if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (item != that.item) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = eventType != null ? eventType.hashCode() : 0;
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
