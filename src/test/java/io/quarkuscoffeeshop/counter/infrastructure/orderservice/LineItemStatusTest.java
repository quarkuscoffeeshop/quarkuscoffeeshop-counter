package io.quarkuscoffeeshop.counter.infrastructure.orderservice;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkuscoffeeshop.counter.domain.LineItem;
import io.quarkuscoffeeshop.counter.domain.LineItemStatus;
import io.quarkuscoffeeshop.counter.domain.Order;
import io.quarkuscoffeeshop.counter.domain.OrderRepository;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import io.quarkuscoffeeshop.infrastructure.OrderService;
import io.quarkuscoffeeshop.counter.domain.TestUtil;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This test verifies that the status of LineItems are accurate when entered and updated.
 * The initial status should be "IN_PROGRESS"
 * After a TicketUp is received the status should change to "FULFILLED"
 * OrderService is the object under test because it orchestrates the creation and persistence of the Order, the parent object of the LineItem
 */
@QuarkusTest
@TestProfile(OrderServiceTestProfile.class)
public class LineItemStatusTest {

    final String orderId = UUID.randomUUID().toString();

    @Inject
    OrderService orderService;

    @Inject
    OrderRepository orderRepository;

    @Test @TestTransaction
    public void testStatusAfterOrderIsPlaced() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand(orderId);
        orderService.onOrderIn(placeOrderCommand);
        await().atLeast(2, TimeUnit.SECONDS);

        long count = orderRepository.count();
        assertEquals(1, count);

        Order order = orderRepository.findById(orderId);
        assertNotNull(order);
        assertTrue(order.getBaristaLineItems().isPresent());
        assertEquals(1, order.getBaristaLineItems().get().size());
        assertEquals(LineItemStatus.IN_PROGRESS, order.getBaristaLineItems().get().get(0).getLineItemStatus());


        LineItem lineItem = order.getBaristaLineItems().get().get(0);
        final TicketUp ticketUp = new TicketUp(
                orderId,
                lineItem.getItemId(),
                lineItem.getItem(),
                lineItem.getName(),
                "Bart"
        );

        orderService.onOrderUp(ticketUp);

        await().atLeast(2, TimeUnit.SECONDS);

        Order updatedOrder = orderRepository.findById(orderId);
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.getBaristaLineItems().isPresent());
        assertEquals(1, updatedOrder.getBaristaLineItems().get().size());
        assertEquals(LineItemStatus.FULFILLED, updatedOrder.getBaristaLineItems().get().get(0).getLineItemStatus());
    }
}

