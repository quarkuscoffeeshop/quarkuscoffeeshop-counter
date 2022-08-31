package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderCreationTest {

    @Test
    public void testOrderCreationWithSingleBeverage() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getBaristaLineItems().get().size());
        assertFalse(order.getKitchenLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithSingleCroissant() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandSingleCroissant();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getKitchenLineItems().get().size());
        assertFalse(order.getBaristaLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithBeverageAndKitchenItems() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandBlackCoffeeAndCroissant();
        Order order = Order.fromPlaceOrderCommand(placeOrderCommand);
        assertNotNull(order);
        assertNotNull(order.getOrderId());
        assertEquals(1, order.getBaristaLineItems().get().size());
        assertEquals(1, order.getKitchenLineItems().get().size());
    }
}
