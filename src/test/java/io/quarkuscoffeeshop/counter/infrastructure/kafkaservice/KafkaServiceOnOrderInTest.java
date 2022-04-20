package io.quarkuscoffeeshop.counter.infrastructure.kafkaservice;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest @Transactional @TestProfile(KafkaOrderInProfile.class)
public class KafkaServiceOnOrderInTest {

    @ConfigProperty(name = "mp.messaging.incoming.orders-in.topic")
    protected String ORDERS_IN;

    // this is being Mocked by OrderServiceMock to avoid database dependencies
    @InjectSpy
    OrderService orderService;

    @Inject
    @Any
    InMemoryConnector connector;

    /**
     * Verify that the appropriate method is called on OrderService when a PlaceOrderCommand is received
     * @see OrderService
     * @see PlaceOrderCommand
     *
     */
    @Test
    public void testOrderIn() {

        PlaceOrderCommand placeOrderCommand = TestUtil.stubPlaceOrderCommand();
        InMemorySource<PlaceOrderCommand> ordersIn = connector.source(ORDERS_IN);
        ordersIn.send(placeOrderCommand);
        await().atLeast(2, TimeUnit.SECONDS);
        verify(orderService, times(1)).onOrderIn(any(PlaceOrderCommand.class));
    }

}
