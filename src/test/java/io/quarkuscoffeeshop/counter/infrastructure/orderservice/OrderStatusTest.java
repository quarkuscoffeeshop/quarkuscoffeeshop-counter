package io.quarkuscoffeeshop.counter.infrastructure.orderservice;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkuscoffeeshop.counter.domain.*;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This test verifies that the status of an Order is accurate when entered and updated.
 * The initial status should be "IN_PROGRESS"
 * After a TicketUp is received the status should change to "FULFILLED"
 * OrderService is the object under test because it orchestrates the creation and persistence of the Order, the parent object of the LineItem
 */
@QuarkusTest
@Transactional
@TestProfile(OrderServiceTestProfile.class)
public class OrderStatusTest {

    final String orderId = UUID.randomUUID().toString();

    @Inject
    OrderService orderService;

    @Inject
    OrderRepository orderRepository;

    @Test
    public void testStatusAfterOrderIsPlaced() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand(orderId);
        orderService.onOrderIn(placeOrderCommand);

        await().atLeast(2, TimeUnit.SECONDS);

        Order order = orderRepository.findById(orderId);
        assertNotNull(order);
        assertTrue(order.getBaristaLineItems().isPresent());
        assertEquals(1, order.getBaristaLineItems().get().size());
        assertEquals(OrderStatus.IN_PROGRESS, order.getOrderStatus());


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
        assertEquals(OrderStatus.FULFILLED, updatedOrder.getOrderStatus());
    }
}
