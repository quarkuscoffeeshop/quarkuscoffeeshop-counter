package io.quarkuscoffeeshop.counter.domain;

import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;
import io.quarkuscoffeeshop.counter.domain.events.LoyaltyMemberPurchaseEvent;
import io.quarkuscoffeeshop.counter.domain.events.OrderCreatedEvent;
import io.quarkuscoffeeshop.counter.domain.events.OrderUpdatedEvent;
import io.quarkuscoffeeshop.counter.domain.valueobjects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;


public class Order {

  @Transient
  static Logger logger = LoggerFactory.getLogger(Order.class);

  private OrderRecord orderRecord;

  protected static Order fromOrderRecord(OrderRecord orderRecord) {
    Order order = new Order();
    order.orderRecord = orderRecord;
    return order;
  }

  /**
   * Each time a TicketUp is received the Order should be checked for completion.
   * An Order is complete when every LineItem is fulfilled.
   *
   * @param ticketUp
   * @return OrderEventResult
   */
  public OrderEventResult applyOrderTicketUp(final TicketUp ticketUp) {

    // set the LineItem's new status
    if (this.getBaristaLineItems().isPresent()) {
      this.getBaristaLineItems().get().stream().forEach(lineItem -> {
        if(lineItem.getItemId().equals(ticketUp.lineItemId)){
          lineItem.setLineItemStatus(LineItemStatus.FULFILLED);
        }
      });
    }
    if (this.getKitchenLineItems().isPresent()) {
      this.getKitchenLineItems().get().stream().forEach(lineItem -> {
        if(lineItem.getItemId().equals(ticketUp.lineItemId)){
          lineItem.setLineItemStatus(LineItemStatus.FULFILLED);
        }
      });
    }

    // if there are both barista and kitchen items concatenate them before checking status
    if (this.getBaristaLineItems().isPresent() && this.getKitchenLineItems().isPresent()) {
      // check the status of the Order itself and update if necessary
      if(Stream.concat(this.getBaristaLineItems().get().stream(), this.getKitchenLineItems().get().stream())
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    } else if (this.getBaristaLineItems().isPresent()) {
      if(this.getBaristaLineItems().get().stream()
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    }else if (this.getKitchenLineItems().isPresent()) {
      if(this.getKitchenLineItems().get().stream()
              .allMatch(lineItem -> {
                return lineItem.getLineItemStatus().equals(LineItemStatus.FULFILLED);
              })){
        this.setOrderStatus(OrderStatus.FULFILLED);
      };
    }

    // create the domain event
    OrderUpdatedEvent orderUpdatedEvent = OrderUpdatedEvent.of(this);

    // create the update value object
    OrderUpdate orderUpdate = new OrderUpdate(ticketUp.getOrderId(), ticketUp.getLineItemId(), ticketUp.getName(), ticketUp.getItem(), OrderStatus.FULFILLED, ticketUp.madeBy);

    OrderEventResult orderEventResult = new OrderEventResult();
    orderEventResult.setOrder(this);
    orderEventResult.addEvent(orderUpdatedEvent);
    orderEventResult.setOrderUpdates(new ArrayList<>() {{
      add(orderUpdate);
    }});
    return orderEventResult;
  }

  /**
   * Create a new Order from a PlaceOrderCommand
   *
   * @param placeOrderCommand
   * @return
   */
  protected static Order fromPlaceOrderCommand(final PlaceOrderCommand placeOrderCommand) {

    logger.debug("creating a new Order from: {}", placeOrderCommand);

    // build the order from the PlaceOrderCommand
    Order order = new Order(placeOrderCommand.getId());
    order.setOrderSource(placeOrderCommand.getOrderSource());
    order.setLocation(placeOrderCommand.getLocation());
    order.setTimestamp(placeOrderCommand.getTimestamp());
    order.setOrderStatus(OrderStatus.IN_PROGRESS);
    if (placeOrderCommand.getLoyaltyMemberId().isPresent()) {
      order.setLoyaltyMemberId(placeOrderCommand.getLoyaltyMemberId().get());
    }

    if (placeOrderCommand.getBaristaLineItems().isPresent()) {
      placeOrderCommand.getBaristaLineItems().get().forEach(commandItem -> {
        logger.debug("createOrderFromCommand adding baristaItem from {}", commandItem.toString());
        LineItem lineItem = new LineItem(commandItem.getItem(), commandItem.getName(), commandItem.getPrice(), LineItemStatus.IN_PROGRESS, order.getOrderRecord());
        order.addBaristaLineItem(lineItem);
      });
    }

    if (placeOrderCommand.getKitchenLineItems().isPresent()) {
      logger.debug("createOrderFromCommand adding kitchenOrders {}", placeOrderCommand.getKitchenLineItems().get().size());
      placeOrderCommand.getKitchenLineItems().get().forEach(commandItem -> {
        LineItem lineItem = new LineItem(commandItem.getItem(), commandItem.getName(), commandItem.getPrice(), LineItemStatus.IN_PROGRESS, order.getOrderRecord());
        order.addKitchenLineItem(lineItem);
      });
    }

    return order;
  }

  private static List<OrderUpdate> createOrderUpdates(Order order) {

    List<OrderUpdate> orderUpdates = new ArrayList<>();

    // create required BaristaTicket, KitchenTicket, and OrderUpdate value objects
    if (order.getBaristaLineItems().isPresent()) {
      order.getBaristaLineItems().get().forEach(lineItem -> {
        orderUpdates.add(new OrderUpdate(order.getOrderId(), lineItem.getItemId(), lineItem.getName(), lineItem.getItem(), OrderStatus.IN_PROGRESS));
      });
    }
    if (order.getKitchenLineItems().isPresent()) {
      order.getKitchenLineItems().get().forEach(lineItem -> {
        orderUpdates.add(new OrderUpdate(order.getOrderId(), lineItem.getItemId(), lineItem.getName(), lineItem.getItem(), OrderStatus.IN_PROGRESS));
      });
    }
    return orderUpdates;
  }

  private static List<OrderTicket> createOrderTickets(String orderId, List<LineItem> lineItems) {
    List<OrderTicket> orderTickets = new ArrayList<>(lineItems.size());
    lineItems.forEach(lineItem -> {
      orderTickets.add(new OrderTicket(orderId, lineItem.getItemId(), lineItem.getItem(), lineItem.getName()));
    });
    return orderTickets;
  }
  /**
   * Creates and returns a new OrderEventResult containing the Order aggregate built from the PlaceOrderCommand
   * and an OrderCreatedEvent
   *
   * @param placeOrderCommand PlaceOrderCommand
   * @return OrderEventResult
   */
  public static OrderEventResult createFromCommand(final PlaceOrderCommand placeOrderCommand) {

    Order order = Order.fromPlaceOrderCommand(placeOrderCommand);

    // create the return value
    OrderEventResult orderEventResult = new OrderEventResult();
    orderEventResult.setOrder(order);

    // create required BaristaTicket, KitchenTicket, and OrderUpdate value objects
    if (order.getBaristaLineItems().isPresent()) {
      orderEventResult.setBaristaTickets(createOrderTickets(order.getOrderId(), order.getBaristaLineItems().get()));
    }

    if (order.getKitchenLineItems().isPresent()) {
      orderEventResult.setKitchenTickets(createOrderTickets(order.getOrderId(), order.getKitchenLineItems().get()));
    }

    // add updates
    orderEventResult.setOrderUpdates(createOrderUpdates(order));

    orderEventResult.addEvent(OrderCreatedEvent.of(order));

    // if this order was placed by a Loyalty Member add the appropriate event
    if (placeOrderCommand.getLoyaltyMemberId().isPresent()) {
      orderEventResult.addEvent(LoyaltyMemberPurchaseEvent.of(order));
    }

    logger.debug("returning {}", orderEventResult);
    return orderEventResult;
  }


  /**
   * Convenience method to prevent Null Pointer Exceptions
   *
   * @param lineItem
   */
  public void addBaristaLineItem(LineItem lineItem) {
    if (getBaristaLineItems().isPresent()) {
      lineItem.setOrder(this.orderRecord);
      this.getBaristaLineItems().get().add(lineItem);
    }else{
      if (this.orderRecord.getBaristaLineItems() == null) {
        this.orderRecord.setBaristaLineItems(new ArrayList<LineItem>(){{ add(lineItem); }});
      }else{
        this.orderRecord.getBaristaLineItems().add(lineItem);
      }
    }
  }

  /**
   * Convenience method to prevent Null Pointer Exceptions
   *
   * @param lineItem
   */
  public void addKitchenLineItem(LineItem lineItem) {
    if (this.getKitchenLineItems().isPresent()) {
      lineItem.setOrder(this.orderRecord);
      this.getKitchenLineItems().get().add(lineItem);
    }else {
      if (this.orderRecord.getKitchenLineItems() == null) {
        this.orderRecord.setKitchenLineItems(new ArrayList<LineItem>(){{ add(lineItem); }});
      }else{
        this.orderRecord.getKitchenLineItems().add(lineItem);
      }
    }
  }

  public Optional<List<LineItem>> getBaristaLineItems() {
    return Optional.ofNullable(this.orderRecord.getBaristaLineItems());
  }

  public void setBaristaLineItems(List<LineItem> baristaLineItems) {
    this.orderRecord.setBaristaLineItems(baristaLineItems);
  }

  public Optional<List<LineItem>> getKitchenLineItems() {
    return Optional.ofNullable(this.orderRecord.getKitchenLineItems());
  }

  public void setKitchenLineItems(List<LineItem> kitchenLineItems) {
    this.orderRecord.setKitchenLineItems(kitchenLineItems);
  }

  public Optional<String> getLoyaltyMemberId() {
    return Optional.ofNullable(this.orderRecord.getLoyaltyMemberId());
  }

  public void setLoyaltyMemberId(String loyaltyMemberId) {
    this.orderRecord.setLoyaltyMemberId(loyaltyMemberId);
  }

  public Order() {
    this.orderRecord = new OrderRecord();
    this.orderRecord.setOrderId(UUID.randomUUID().toString());
    this.orderRecord.setTimestamp(Instant.now());
  }

  public Order(final String orderId){
    this.orderRecord = new OrderRecord();
    this.orderRecord.setOrderId(orderId);
    this.orderRecord.setTimestamp(Instant.now());
  }

  public Order(final String orderId, final OrderSource orderSource, final Location location, final String loyaltyMemberId, final Instant timestamp, final OrderStatus orderStatus, final List<LineItem> baristaLineItems, final List<LineItem> kitchenLineItems) {
    this.orderRecord.setOrderId(orderId);
    this.orderRecord.setOrderSource(orderSource);
    this.orderRecord.setLocation(location);
    this.orderRecord.setLoyaltyMemberId(loyaltyMemberId);
    this.orderRecord.setTimestamp(timestamp);
    this.orderRecord.setOrderStatus(orderStatus);
    this.orderRecord.setBaristaLineItems(baristaLineItems);
    this.orderRecord.setKitchenLineItems(kitchenLineItems);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
            .add("orderId='" + orderRecord.getOrderId() + "'")
            .add("orderSource=" + orderRecord.getOrderSource())
            .add("loyaltyMemberId='" + orderRecord.getLoyaltyMemberId() + "'")
            .add("timestamp=" + orderRecord.getTimestamp())
            .add("orderStatus=" + orderRecord.getOrderStatus())
            .add("location=" + orderRecord.getLocation())
            .add("baristaLineItems=" + orderRecord.getBaristaLineItems())
            .add("kitchenLineItems=" + orderRecord.getKitchenLineItems())
            .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Order order = (Order) o;

    return orderRecord != null ? orderRecord.equals(order.orderRecord) : order.orderRecord == null;
  }

  @Override
  public int hashCode() {
    return orderRecord != null ? orderRecord.hashCode() : 0;
  }

  public String getOrderId() {
    return this.orderRecord.getOrderId();
  }

  public OrderSource getOrderSource() {
    return this.orderRecord.getOrderSource();
  }

  public void setOrderSource(OrderSource orderSource) {
    this.orderRecord.setOrderSource(orderSource);
  }

  public Location getLocation() {
    return this.orderRecord.getLocation();
  }

  public void setLocation(Location location) {
    this.orderRecord.setLocation(location);
  }

  public OrderStatus getOrderStatus() {
    return this.orderRecord.getOrderStatus();
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderRecord.setOrderStatus(orderStatus);
  }

  public Instant getTimestamp() {
    return this.orderRecord.getTimestamp();
  }

  public void setTimestamp(Instant timestamp) {
    this.orderRecord.setTimestamp(timestamp);
  }

  protected OrderRecord getOrderRecord() {
    return this.orderRecord;
  }
}
