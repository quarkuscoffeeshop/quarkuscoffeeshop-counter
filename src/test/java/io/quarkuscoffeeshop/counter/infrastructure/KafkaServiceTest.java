package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import io.quarkuscoffeeshop.infrastructure.OrderService;
import io.quarkuscoffeeshop.testing.TestUtil;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@QuarkusTest @QuarkusTestResource(KafkaTestResource.class) @Transactional
public class KafkaServiceTest {

    @ConfigProperty(name = "mp.messaging.incoming.orders-in.topic")
    protected String ORDERS_IN;

    @ConfigProperty(name = "mp.messaging.incoming.orders-up.topic")
    protected String ORDERS_UP;

    @InjectSpy
    OrderService mock;

    @Inject
    @Any
    InMemoryConnector connector;

    @Test
    /**
     * Verify that the appropriate method is called on OrderService when a PlaceOrderCommand is received
     * @see OrderService
     * @see PlaceOrderCommand
     *
     */
    public void testOrderIn() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        InMemorySource<PlaceOrderCommand> ordersIn = connector.source(ORDERS_IN);
        ordersIn.send(placeOrderCommand);
        await().atLeast(2, TimeUnit.SECONDS);
        verify(mock).onOrderIn(any(PlaceOrderCommand.class));
    }

/*
    @Test
    */
/**
     * Verify that the appropriate method is called on OrderService when an OrderTicket is received
     * @see OrderService
     * @see OrderTicket
     *
     *//*

    public void testOrderUp() {

        TicketUp orderTicketUp = TestUtil.stubOrderTicketUp();
        InMemorySource<TicketUp> ordersUp = connector.source(ORDERS_UP);
        ordersUp.send(orderTicketUp);
        await().atLeast(2, TimeUnit.SECONDS);
        Mockito.verify(orderService).onOrderUp(any(TicketUp.class));
    }

*/
}
