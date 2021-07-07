package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.Mock;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import io.quarkuscoffeeshop.infrastructure.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@Alternative
@ApplicationScoped
public class OrderServiceMock extends OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceMock.class);

    @Override
    public void onOrderIn(final PlaceOrderCommand placeOrderCommand){

        logger.info("onOrderIn called on Mock: {}", placeOrderCommand);
    }

    @Override
    public void onOrderUp(final TicketUp ticketUp) {

        logger.info("onOrderUp called on Mock: {}", ticketUp);
    }
}
