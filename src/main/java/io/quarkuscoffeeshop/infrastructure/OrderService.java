package io.quarkuscoffeeshop.infrastructure;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkuscoffeeshop.counter.domain.Order;
import io.quarkuscoffeeshop.counter.domain.OrderRepository;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderEventResult;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderTicket;
import io.quarkuscoffeeshop.counter.domain.valueobjects.OrderUpdate;
import io.quarkuscoffeeshop.counter.domain.valueobjects.TicketUp;
import org.eclipse.microprofile.context.ThreadContext;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Inject
    ThreadContext threadContext;

    @Inject
    OrderRepository orderRepository;

    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Channel("barista")
    Emitter<OrderTicket> baristaEmitter;

    @Channel("kitchen")
    Emitter<OrderTicket> kitchenEmitter;

    @Channel("web-updates")
    Emitter<OrderUpdate> orderUpdateEmitter;

    public void onOrderIn(final PlaceOrderCommand placeOrderCommand) {

        logger.debug("onOrderIn {}", placeOrderCommand);

        OrderEventResult orderEventResult = Order.createFromCommand(placeOrderCommand);

        logger.debug("OrderEventResult returned: {}", orderEventResult);

        orderRepository.persist(orderEventResult.getOrder());

        orderEventResult.getOutboxEvents().forEach(exportedEvent -> {
            logger.debug("Firing event: {}", exportedEvent);
            event.fire(exportedEvent);
        });

        if (orderEventResult.getBaristaTickets().isPresent()) {
            orderEventResult.getBaristaTickets().get().forEach(baristaTicket -> {
                logger.debug("Sending Ticket to Barista Service: {}", baristaTicket);
                baristaEmitter.send(baristaTicket);
            });
        }

        if (orderEventResult.getKitchenTickets().isPresent()) {
            orderEventResult.getKitchenTickets().get().forEach(kitchenTicket -> {
                kitchenEmitter.send(kitchenTicket);
            });
        }

        orderEventResult.getOrderUpdates().forEach(orderUpdate -> {
            orderUpdateEmitter.send(orderUpdate);
        });

    }

    @Transactional
    public void onOrderUp(final TicketUp ticketUp) {

         logger.debug("onOrderUp: {}", ticketUp);
        Order order = orderRepository.findById(ticketUp.getOrderId());
        OrderEventResult orderEventResult = order.applyOrderTicketUp(ticketUp);
        logger.debug("OrderEventResult returned: {}", orderEventResult);
        orderRepository.persist(orderEventResult.getOrder());
        orderEventResult.getOrderUpdates().forEach(orderUpdate -> {
            orderUpdateEmitter.send(orderUpdate);
        });
        orderEventResult.getOutboxEvents().forEach(exportedEvent -> {
            event.fire(exportedEvent);
        });
    }

    @Override
    public String toString() {
        return "OrderService{" +
                "threadContext=" + threadContext +
                ", orderRepository=" + orderRepository +
                ", event=" + event +
                ", baristaEmitter=" + baristaEmitter +
                ", kitchenEmitter=" + kitchenEmitter +
                ", orderUpdateEmitter=" + orderUpdateEmitter +
                '}';
    }

}
