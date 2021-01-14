package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.domain.Item;
import io.quarkuscoffeeshop.domain.LineItem;
import io.quarkuscoffeeshop.domain.OrderSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceiptTest {

    Logger logger = LoggerFactory.getLogger(ReceiptTest.class);


    @Test
    public void testToString() {
        Order order = mockOrder();
        Receipt receipt = Order.createReceipt(order);
        logger.debug("receipt {}", receipt);
        System.out.println(receipt);
        System.out.println(JsonUtil.toJson(receipt));
    }

    private Order mockOrder() {

        Order retVal = new Order();
        retVal.id = UUID.randomUUID().toString();
        retVal.orderSource = OrderSource.COUNTER;
        retVal.beverageLineItems = mockBaristaItems();
        return retVal;
    }

    private List<LineItem> mockBaristaItems() {
        return new ArrayList<>(){{
            add(new LineItem(Item.ESPRESSO, "Lemmy"));
        }};
    }
}
