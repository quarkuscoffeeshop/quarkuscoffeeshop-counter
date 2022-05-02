package io.quarkuscoffeeshop.testing;

import io.quarkuscoffeeshop.counter.domain.Item;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PlaceOrderCommandTestUtilTest {


    @Test
    public void testIt() {

        PlaceOrderCommand placeOrderCommand = new PlaceOrderCommandTestUtil().withBlackCoffee().build();
        assertNotNull(placeOrderCommand);
        assertNotNull(placeOrderCommand.getId());
        assertEquals(1, placeOrderCommand.getBaristaLineItems().get().size());
        assertEquals(Item.COFFEE_BLACK, placeOrderCommand.getBaristaLineItems().get().get(0).item);
    }
}
