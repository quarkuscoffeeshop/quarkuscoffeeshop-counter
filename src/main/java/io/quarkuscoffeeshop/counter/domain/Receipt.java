package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.hibernate.annotations.Cascade;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Entity @Table(name = "Receipts")
public class Receipt extends PanacheEntityBase {

    @Id @Column(nullable = false, name = "orderId")
    private String orderId;

    private BigDecimal total;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<ReceiptLineItem> lineItems;

    public Receipt(String orderId, BigDecimal total, List<ReceiptLineItem> lineItems) {
        this.orderId = orderId;
        this.total = total;
        this.lineItems = lineItems;
    }

    public Receipt() {

    }

    public void addLineItem(final ReceiptLineItem receiptLineItem) {
        if (lineItems == null) {
            lineItems = new ArrayList<>();
        }
        lineItems.add(receiptLineItem);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Receipt.class.getSimpleName() + "[", "]")
                .add("orderId=" + orderId)
                .add("total=" + total)
                .add("lineItems=" + lineItems)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Receipt receipt = (Receipt) o;
        return Objects.equals(total, receipt.total) &&
                Objects.equals(lineItems, receipt.lineItems);
    }


    @Override
    public int hashCode() {
        return Objects.hash(orderId, total);
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<ReceiptLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<ReceiptLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
