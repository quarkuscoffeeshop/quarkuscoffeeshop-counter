package io.quarkuscoffeeshop.counter.infrastructure.kafkaservice;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import io.quarkuscoffeeshop.counter.infrastructure.OrderServiceMock;
import io.quarkuscoffeeshop.infrastructure.OrderService;
import io.quarkuscoffeeshop.counter.domain.TestUtil;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest @Transactional @TestProfile(KafkaOrderUpProfile.class)
public class KafkaServiceOnOrderUpTest implements QuarkusTestProfile {

    @ConfigProperty(name = "mp.messaging.incoming.orders-up.topic")
    protected String ORDERS_UP;

    @InjectSpy
    OrderService orderService;

    @Inject
    @Any
    InMemoryConnector connector;

    /**
     * Verify that the appropriate method is called on OrderService when a TicketUp is received
     * @see TicketUp
     * @see PlaceOrderCommand
     *
     */
    @Test
    public void testOrderUp() {

        TicketUp orderTicketUp = TestUtil.stubOrderTicketUp();
        InMemorySource<TicketUp> ordersUp = connector.source(ORDERS_UP);
        ordersUp.send(orderTicketUp);
        await().atLeast(2, TimeUnit.SECONDS);
        verify(orderService, times(1)).onOrderUp(any(TicketUp.class));
    }
}
