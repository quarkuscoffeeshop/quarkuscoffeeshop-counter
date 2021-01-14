package io.quarkuscoffeeshop.counter.domain.valueobjects;

import io.quarkuscoffeeshop.counter.domain.Item;

import java.time.Instant;

/**
 * Java representation of the JSON sent and received from Barista and Kitchen
 *
 */
public class OrderTicket {

  private final String orderId;

  private final String lineItemId;

  private final Item item;

  private final String name;

  private final Instant timestamp;

  public OrderTicket(String orderId, String lineItemId, Item item, String name) {
    this.orderId = orderId;
    this.lineItemId = lineItemId;
    this.item = item;
    this.name = name;
    this.timestamp = Instant.now();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OrderTicket)) return false;

    OrderTicket orderTicket = (OrderTicket) o;

    if (orderId != null ? !orderId.equals(orderTicket.orderId) : orderTicket.orderId != null) return false;
    if (lineItemId != null ? !lineItemId.equals(orderTicket.lineItemId) : orderTicket.lineItemId != null) return false;
    if (item != orderTicket.item) return false;
    if (name != null ? !name.equals(orderTicket.name) : orderTicket.name != null) return false;
    return timestamp != null ? timestamp.equals(orderTicket.timestamp) : orderTicket.timestamp == null;
  }

  @Override
  public int hashCode() {
    int result = orderId != null ? orderId.hashCode() : 0;
    result = 31 * result + (lineItemId != null ? lineItemId.hashCode() : 0);
    result = 31 * result + (item != null ? item.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "OrderTicket{" +
      "orderId='" + orderId + '\'' +
      ", id=" + lineItemId +
      ", item=" + item +
      ", name='" + name + '\'' +
      ", timestamp=" + timestamp +
      '}';
  }

  public String getOrderId() {
    return orderId;
  }

  public String getLineItemId() {
    return lineItemId;
  }

  public Item getItem() {
    return item;
  }

  public String getName() {
    return name;
  }

  public Instant getTimestamp() {
    return timestamp;
  }
}
