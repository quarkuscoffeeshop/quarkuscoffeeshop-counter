package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.domain.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class OrderTest {

    @Test
    public void testOrderCreatedEventFromBeveragesOnly() {

        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(
                OrderSource.WEB,
                new ArrayList<OrderLineItem>(){{
                    add(new OrderLineItem(Item.CAPPUCCINO, Double.valueOf(3.75), "Kirk"));
                    add(new OrderLineItem(Item.COFFEE_WITH_ROOM, Double.valueOf(3.75),"Spock"));
                }},
                null);
        OrderCreatedEvent orderCreatedEvent = Order.handlePlaceOrderCommand(placeOrderCommand);
        Assert.assertNotNull(orderCreatedEvent);
        Assert.assertNotNull(orderCreatedEvent.events);
        Assert.assertEquals(2, orderCreatedEvent.events.size());
        orderCreatedEvent.events.forEach(e -> {
            Assert.assertEquals(OrderInEvent.class, e.getClass());
            Assert.assertTrue(e.name.equals("Kirk") || e.name.equals("Spock"));
            Assert.assertEquals(EventType.BEVERAGE_ORDER_IN, e.eventType);
        });
    }

    @Test
    public void testProcessCreateOrderCommandFoodOnly() {

        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(
                OrderSource.WEB,
                null,
                new ArrayList<OrderLineItem>(){{
                    add(new OrderLineItem(Item.CROISSANT, Double.valueOf(3.75), "Kirk"));
                    add(new OrderLineItem(Item.CAKEPOP, Double.valueOf(3.75),"Spock"));
                }});
        OrderCreatedEvent orderCreatedEvent = Order.handlePlaceOrderCommand(placeOrderCommand);

        Assert.assertNotNull(orderCreatedEvent);
        Assert.assertNotNull(orderCreatedEvent.events);
        Assert.assertEquals(2, orderCreatedEvent.events.size());
        orderCreatedEvent.events.forEach(e -> {
            Assert.assertEquals(OrderInEvent.class, e.getClass());
            Assert.assertTrue(e.name.equals("Kirk") || e.name.equals("Spock"));
            Assert.assertEquals(EventType.KITCHEN_ORDER_IN, e.eventType);
        });
    }

    @Test
    public void testOrderInBeveragesAndFood() {

        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(
                OrderSource.WEB,
                new ArrayList<OrderLineItem>(){{
                    add(new OrderLineItem(Item.CAPPUCCINO, Double.valueOf(3.75), "Kirk"));
                    add(new OrderLineItem(Item.COFFEE_WITH_ROOM, Double.valueOf(3.75),"Spock"));
                }},
                new ArrayList<OrderLineItem>(){{
                    add(new OrderLineItem(Item.CROISSANT, Double.valueOf(3.75), "Kirk"));
                    add(new OrderLineItem(Item.CAKEPOP, Double.valueOf(3.75),"Spock"));
                }});
        OrderCreatedEvent orderCreatedEvent = Order.handlePlaceOrderCommand(placeOrderCommand);

        Assert.assertNotNull(orderCreatedEvent);
        Assert.assertNotNull(orderCreatedEvent.events);
        Assert.assertEquals(4, orderCreatedEvent.events.size());
        int beveragOrders = 0;
        int kitchenOrders = 0;
        orderCreatedEvent.events.forEach(e -> {
            Assert.assertEquals(OrderInEvent.class, e.getClass());
            Assert.assertTrue(e.name.equals("Kirk") || e.name.equals("Spock"));
        });
        Assert.assertEquals(2, orderCreatedEvent.events.stream().filter(
                e -> e.eventType.equals(EventType.KITCHEN_ORDER_IN)).count());
        Assert.assertEquals(2, orderCreatedEvent.events.stream().filter(
                e -> e.eventType.equals(EventType.BEVERAGE_ORDER_IN)).count());
    }

}
