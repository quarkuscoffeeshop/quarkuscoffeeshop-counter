package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.codecs.pojo.annotations.BsonId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@RegisterForReflection
public class Order {

    @Transient
    static final Logger logger = LoggerFactory.getLogger(Order.class);

    @BsonId
    public String id;

    OrderSource orderSource;

    public String rewardsId;

    public List<LineItem> beverageLineItems = new ArrayList<>();

    public List<LineItem> kitchenLineItems = new ArrayList<>();

    public Order() {
    }

    public Order(List<LineItem> beverageLineItems) {
        this.beverageLineItems = beverageLineItems;
    }

    public static OrderCreatedEvent process(final PlaceOrderCommand placeOrderCommand) {
        Order order = createOrderFromCommand(placeOrderCommand);
        return createOrderCreatedEvent(order);
    }

    public static Receipt createReceipt(final Order order) {
        Receipt receipt = new Receipt();
        order.beverageLineItems.forEach(beverageLineItem -> {
            ReceiptLineItem receiptLineItem = new ReceiptLineItem(receipt, beverageLineItem.item, beverageLineItem.name);
            receipt.addLineItem(receiptLineItem);
        });
        order.kitchenLineItems.forEach(beverageLineItem -> {
            ReceiptLineItem receiptLineItem = new ReceiptLineItem(receipt, beverageLineItem.item, beverageLineItem.name);
        });
        return receipt;
    }

    /*
        Creates the Value Objects associated with a new Order
     */
    private static OrderCreatedEvent createOrderCreatedEvent(final Order order) {

        // construct the OrderCreatedEvent
        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
        orderCreatedEvent.order = order;

        if (order.getBeverageLineItems().size() >= 1) {
            order.beverageLineItems.forEach(b -> {
                orderCreatedEvent.addEvent(new OrderInEvent(EventType.BEVERAGE_ORDER_IN, order.id, b.name, b.item));
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

    private static Order createOrderFromCommand(final PlaceOrderCommand placeOrderCommand) {
        logger.debug("received PlaceOrderCommand: {}", placeOrderCommand.toString());

        // build the order from the CreateOrderCommand
        Order order = new Order();
        order.id = placeOrderCommand.getId();
        order.orderSource = placeOrderCommand.getOrderSource();
        if (placeOrderCommand.getRewardsId().isPresent()) {
            order.rewardsId = placeOrderCommand.getRewardsId().get();
        }
        if (placeOrderCommand.getBaristaItems().isPresent()) {
            logger.debug("createOrderFromCommand adding beverages {}", placeOrderCommand.getBaristaItems().get().size());
            placeOrderCommand.getBaristaItems().get().forEach(v -> {
                logger.debug("createOrderFromCommand adding baristaItem from {}", v.toString());
                order.getBeverageLineItems().add(new LineItem(v.getItem(), v.getName()));
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

    public List<LineItem> getBeverageLineItems() {
        return beverageLineItems;
    }

    public List<LineItem> getKitchenLineItems() {
        return kitchenLineItems;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id:", this.id)
                .append("rewardsIf:", this.rewardsId)
                .append("beverageLineItems", beverageLineItems.toString())
                .append("kitchenLineItems", kitchenLineItems.toString()).toString();
    }
}
