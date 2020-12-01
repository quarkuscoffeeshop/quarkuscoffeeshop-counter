package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkuscoffeeshop.counter.domain.*;
import io.quarkuscoffeeshop.counter.domain.commands.*;
import io.quarkuscoffeeshop.counter.domain.events.*;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RegisterForReflection
public class Order {

    @Transient
    static final Logger logger = LoggerFactory.getLogger(Order.class);

    @BsonId
    private String id;

    private OrderSource orderSource;

    private String loyaltyMemberId;

    private List<LineItem> baristaLineItems;

    private List<LineItem> kitchenLineItems;

    public Order() {
    }

    public Order(List<LineItem> baristaLineItems) {
        this.baristaLineItems = baristaLineItems;
    }

    public static OrderResults processPlaceOrderCommand(final PlaceOrderCommand placeOrderCommand) {
        // build the order from the PlaceOrderCommand
        Order order = new Order();
        order.id = placeOrderCommand.getId();
        order.orderSource = placeOrderCommand.getOrderSource();

        List<CoffeeshopEvent> events = new ArrayList<>();

        if (placeOrderCommand.getBaristaLineItems().isPresent()) {
            logger.debug("createOrderFromCommand adding beverages {}", placeOrderCommand.getBaristaLineItems().get().size());
            placeOrderCommand.getBaristaLineItems().get().forEach(v -> {
                logger.debug("createOrderFromCommand adding baristaItem from {}", v.toString());
                order.addBaristaItem(new LineItem(order.getId(), UUID.randomUUID().toString(), v.getItem(), v.getName()));
                events.add(new OrderInEvent(
                        EventType.BARISTA_ORDER_IN,
                        order.getId(),
                        UUID.randomUUID().toString(),
                        v.getName(),
                        v.getItem(),
                        Instant.now()));
            });
        }
        if (placeOrderCommand.getKitchenLineItems().isPresent()) {
            logger.debug("createOrderFromCommand adding kitchenOrders {}", placeOrderCommand.getKitchenLineItems().get().size());
            placeOrderCommand.getKitchenLineItems().get().forEach(v ->{
                logger.debug("createOrderFromCommand adding kitchenItem from {}", v.toString());
                order.addKitchenItem(new LineItem(order.getId(), UUID.randomUUID().toString(), v.getItem(), v.getName()));
                events.add(new OrderInEvent(
                        EventType.KITCHEN_ORDER_IN,
                        order.getId(),
                        UUID.randomUUID().toString(),
                        v.getName(),
                        v.getItem(),
                        Instant.now()));
            });
        }

        return new OrderResults(order, events);
    }

    /**
     * Convenience method to prevent Null Pointer Exceptions
     * @param lineItem
     */
    public void addBaristaItem(LineItem lineItem) {
        if (this.baristaLineItems == null) {
            this.baristaLineItems = new ArrayList<>();
        }
        this.baristaLineItems.add(lineItem);
    }

    /**
     * Convenience method to prevent Null Pointer Exceptions
     * @param lineItem
     */
    public void addKitchenItem(LineItem lineItem) {
        if (this.kitchenLineItems == null) {
            this.kitchenLineItems = new ArrayList<>();
        }
        this.kitchenLineItems.add(lineItem);
    }
/*

    public static Receipt createReceipt(final Order order) {
        Receipt receipt = new Receipt();
        receipt.setOrderId(order.id);
        order.baristaLineItems.forEach(beverageLineItem -> {
            ReceiptLineItem receiptLineItem = new ReceiptLineItem(receipt, beverageLineItem.item, beverageLineItem.name);
            receipt.addLineItem(receiptLineItem);
        });
        order.kitchenLineItems.forEach(beverageLineItem -> {
            ReceiptLineItem receiptLineItem = new ReceiptLineItem(receipt, beverageLineItem.item, beverageLineItem.name);
        });
        return receipt;
    }

*/
    /*
        Creates the Value Objects associated with a new Order
    private static OrderCreatedEvent createOrderCreatedEvent(final Order order) {

        // construct the OrderCreatedEvent
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.order = order;

        if (order.getBaristaLineItems().size() >= 1) {
            order.baristaLineItems.forEach(b -> {
                orderCreatedEvent.addEvent(new OrderInEvent(EventType.BARISTA_ORDER_IN, order.id, b.name, b.item));
            });
        }
        if (order.getKitchenLineItems().size() >= 1) {
            order.kitchenLineItems.forEach(k -> {
                orderCreatedEvent.addEvent(new OrderInEvent(EventType.KITCHEN_ORDER_IN, order.id, k.name, k.item));
            });
        }
        logger.debug("createEventFromCommand: returning OrderCreatedEvent {}", orderCreatedEvent.toString());
        return orderCreatedEvent;
    }
     */

/*
    private static Order createOrderFromCommand(final PlaceOrderCommand placeOrderCommand) {
        logger.debug("received PlaceOrderCommand: {}", placeOrderCommand.toString());

        // build the order from the CreateOrderCommand
        Order order = new Order();
        order.id = placeOrderCommand.getId();
        order.orderSource = placeOrderCommand.getOrderSource();
        if (placeOrderCommand.getLoyaltyMemberId().isPresent()) {
            order.loyaltyMemberId = placeOrderCommand.getLoyaltyMemberId().get();
        }
        if (placeOrderCommand.getBaristaLineItems().isPresent()) {
            logger.debug("createOrderFromCommand adding beverages {}", placeOrderCommand.getBaristaItems().get().size());
            placeOrderCommand.getBaristaLineItems().get().forEach(v -> {
                logger.debug("createOrderFromCommand adding baristaItem from {}", v.toString());
                order.getBaristaLineItems().add(new LineItem(v.getItem(), v.getName()));
            });
        }
        if (placeOrderCommand.getKitchenItems().isPresent()) {
            logger.debug("createOrderFromCommand adding kitchenOrders {}", placeOrderCommand.getKitchenItems().get().size());
            placeOrderCommand.getKitchenItems().get().forEach(v ->{
                logger.debug("createOrderFromCommand adding kitchenItem from {}", v.toString());
                order.getKitchenLineItems().add(new LineItem(v.getItem(), v.getName()));
            });
        }
        return order;
    }
*/

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id:", this.id)
                .append("rewardsIf:", this.loyaltyMemberId)
                .append("beverageLineItems", baristaLineItems.toString())
                .append("kitchenLineItems", kitchenLineItems.toString()).toString();
    }

    public List<LineItem> getBaristaLineItems() {
        return baristaLineItems;
    }

    public List<LineItem> getKitchenLineItems() {
        return kitchenLineItems;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderSource getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(OrderSource orderSource) {
        this.orderSource = orderSource;
    }

    public String getLoyaltyMemberId() {
        return loyaltyMemberId;
    }

    public void setLoyaltyMemberId(String loyaltyMemberId) {
        this.loyaltyMemberId = loyaltyMemberId;
    }
}
