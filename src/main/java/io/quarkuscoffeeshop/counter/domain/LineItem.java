package io.quarkuscoffeeshop.counter.domain;

import java.util.StringJoiner;

public class LineItem {

    private String id;

    private String orderId;

    private Item item;

    private String name;

    public LineItem() {
    }

    public LineItem(String id, String orderId, Item item, String name) {
        this.id = id;
        this.orderId = orderId;
        this.item = item;
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", LineItem.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("orderId='" + orderId + "'")
                .add("item=" + item)
                .add("name='" + name + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineItem lineItem = (LineItem) o;

        if (id != null ? !id.equals(lineItem.id) : lineItem.id != null) return false;
        if (orderId != null ? !orderId.equals(lineItem.orderId) : lineItem.orderId != null) return false;
        if (item != lineItem.item) return false;
        return name != null ? name.equals(lineItem.name) : lineItem.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
}
