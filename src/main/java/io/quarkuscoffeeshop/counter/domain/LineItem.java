package io.quarkuscoffeeshop.counter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.StringJoiner;
import java.util.UUID;

@JsonIgnoreProperties(value = { "orderId" })
@Entity
@Table(name = "LineItems")
public class LineItem extends PanacheEntityBase {

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "order_id", nullable = false)
  OrderRecord order;

  @Id
  @Column(nullable = false, unique = true)
  private String itemId;

  @Enumerated(EnumType.STRING)
  private Item item;

  private String name;

  private BigDecimal price;

  @Enumerated(EnumType.STRING)
  private LineItemStatus lineItemStatus;

  public LineItem() {
    this.itemId = UUID.randomUUID().toString();
  }

  public LineItem(Item item, String name, BigDecimal price, LineItemStatus lineItemStatus, OrderRecord order) {
    this.itemId = UUID.randomUUID().toString();
    this.item = item;
    this.name = name;
    this.price = price;
    this.lineItemStatus = lineItemStatus;
    this.order = order;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LineItem.class.getSimpleName() + "[", "]")
            .add("order=" + order.getOrderId())
            .add("itemId='" + itemId + "'")
            .add("item=" + item)
            .add("name='" + name + "'")
            .add("price=" + price)
            .add("lineItemStatus=" + lineItemStatus)
            .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LineItem lineItem = (LineItem) o;

    if (order != null ? !order.equals(lineItem.order) : lineItem.order != null) return false;
    if (itemId != null ? !itemId.equals(lineItem.itemId) : lineItem.itemId != null) return false;
    if (item != lineItem.item) return false;
    if (name != null ? !name.equals(lineItem.name) : lineItem.name != null) return false;
    if (price != null ? !price.equals(lineItem.price) : lineItem.price != null) return false;
    return lineItemStatus == lineItem.lineItemStatus;
  }

  @Override
  public int hashCode() {
    int result = order != null ? order.hashCode() : 0;
    result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
    result = 31 * result + (item != null ? item.hashCode() : 0);
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (lineItemStatus != null ? lineItemStatus.hashCode() : 0);
    return result;
  }

  public BigDecimal getPrice() {
    return this.item.getPrice();
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LineItemStatus getLineItemStatus() {
    return lineItemStatus;
  }

  public void setLineItemStatus(LineItemStatus lineItemStatus) {
    this.lineItemStatus = lineItemStatus;
  }

  public void setOrder(OrderRecord order) {
    this.order = order;
  }

  public String getItemId() {
    return this.itemId;
  }
}
