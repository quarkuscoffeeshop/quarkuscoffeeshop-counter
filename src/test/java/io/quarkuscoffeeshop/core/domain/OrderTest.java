package io.quarkuscoffeeshop.core.domain;

import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.core.domain.Order;
import io.quarkuscoffeeshop.core.domain.OrderCreatedEvent;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class OrderTest {

    @Test
    public void testOrderCreatedEventFromBeveragesOnly() {

        List<LineItem> beverages = new ArrayList<>();
        beverages.add(new LineItem(Item.COFFEE_WITH_ROOM, "Kirk"));
        beverages.add(new LineItem(Item.ESPRESSO_DOUBLE, "Spock"));
        OrderInCommand orderInCommand = new OrderInCommand(UUID.randomUUID().toString(), beverages, null);
        System.out.println(orderInCommand);
        OrderCreatedEvent orderCreatedEvent = Order.processCreateOrderCommand(orderInCommand);
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

        List<LineItem> foods = new ArrayList<>();
        foods.add(new LineItem(Item.MUFFIN, "Kirk"));
        foods.add(new LineItem(Item.CAKEPOP, "Spock"));
        OrderInCommand orderInCommand = new OrderInCommand(UUID.randomUUID().toString(),null, foods);
        OrderCreatedEvent orderCreatedEvent = Order.processCreateOrderCommand(orderInCommand);

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

        List<LineItem> foods = new ArrayList<>();
        foods.add(new LineItem(Item.MUFFIN, "Kirk"));
        foods.add(new LineItem(Item.CAKEPOP, "Spock"));

        List<LineItem> beverages = new ArrayList<>();
        beverages.add(new LineItem(Item.CAPPUCCINO, "Kirk"));
        beverages.add(new LineItem(Item.COFFEE_BLACK, "Spock"));

        OrderInCommand orderInCommand = new OrderInCommand(UUID.randomUUID().toString(), beverages, foods);
        OrderCreatedEvent orderCreatedEvent = Order.processCreateOrderCommand(orderInCommand);

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
