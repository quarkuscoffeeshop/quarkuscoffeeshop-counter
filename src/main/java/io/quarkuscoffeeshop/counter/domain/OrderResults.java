package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.counter.domain.events.CoffeeshopEvent;

import java.util.List;
import java.util.StringJoiner;

/**
 * Container holding the Order and a List of Events
 */
public class OrderResults {

    private Order order;

    private List<CoffeeshopEvent> eventList;

    public OrderResults(Order order, List<CoffeeshopEvent> eventList) {
        this.order = order;
        this.eventList = eventList;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderResults.class.getSimpleName() + "[", "]")
                .add("order=" + order)
                .add("eventList=" + eventList)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderResults that = (OrderResults) o;

        if (order != null ? !order.equals(that.order) : that.order != null) return false;
        return eventList != null ? eventList.equals(that.eventList) : that.eventList == null;
    }

    @Override
    public int hashCode() {
        int result = order != null ? order.hashCode() : 0;
        result = 31 * result + (eventList != null ? eventList.hashCode() : 0);
        return result;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<CoffeeshopEvent> getEventList() {
        return eventList;
    }

    public void setEventList(List<CoffeeshopEvent> eventList) {
        this.eventList = eventList;
    }
}
