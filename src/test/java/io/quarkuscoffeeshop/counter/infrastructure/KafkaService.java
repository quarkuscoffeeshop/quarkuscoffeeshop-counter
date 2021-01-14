package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.reactive.messaging.annotations.Merge;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    OrderService orderService;

    @Incoming("orders-in")
    @Blocking
    @Transactional
    public void orderIn(final PlaceOrderCommand placeOrderCommand) {

        logger.debug("PlaceOrderCommand received: {}", placeOrderCommand);
        orderService.onOrderIn(placeOrderCommand);
    }

    @Incoming("orders-up")
    @Blocking
    @Transactional
    @Merge
    public void orderUp(final TicketUp ticketUp) {

        logger.debug("TicketUp received: {}", ticketUp);
        orderService.onOrderUp(ticketUp);
    }
}
