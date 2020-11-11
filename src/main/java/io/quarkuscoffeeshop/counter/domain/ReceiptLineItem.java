package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkuscoffeeshop.domain.Item;
import io.quarkuscoffeeshop.domain.LineItem;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

@Entity @Table(name="LineItems")
public class ReceiptLineItem extends PanacheEntity {

    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="orderId",nullable = false)
    private Receipt receipt;

    @Enumerated(EnumType.STRING)
    private Item item;

    private String name;

    public ReceiptLineItem() {
    }

    public ReceiptLineItem(Receipt receipt, Item item, String name) {
        this.receipt = receipt;
        this.item = item;
        this.name = name;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ReceiptLineItem.class.getSimpleName() + "[", "]")
                .add("item=" + item)
                .add("name='" + name + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptLineItem that = (ReceiptLineItem) o;
        return Objects.equals(receipt, that.receipt) &&
                item == that.item &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receipt, item, name);
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
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
