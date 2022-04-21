package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderEventResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderEventResultCreationTest {

    @Test
    public void testOrderCreationWithSingleBeverage() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getBaristaLineItems().get().size());
        assertFalse(orderEventResult.getOrder().getKitchenLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithSingleCroissant() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandSingleCroissant();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getKitchenLineItems().get().size());
        assertFalse(orderEventResult.getOrder().getBaristaLineItems().isPresent());
    }

    @Test
    public void testOrderCreationWithBeverageAndKitchenItems() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommandBlackCoffeeAndCroissant();
        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);
        assertNotNull(orderEventResult.getOrder());
        assertNotNull(orderEventResult.getOrder().getOrderId());
        assertEquals(1, orderEventResult.getOrder().getBaristaLineItems().get().size());
        assertEquals(1, orderEventResult.getOrder().getKitchenLineItems().get().size());
    }

}
