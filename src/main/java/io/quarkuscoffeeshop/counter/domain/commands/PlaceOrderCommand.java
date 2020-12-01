package io.quarkuscoffeeshop.counter.domain.commands;

import io.quarkuscoffeeshop.counter.domain.LineItem;
import io.quarkuscoffeeshop.counter.domain.OrderSource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class PlaceOrderCommand {

    private String id;

    private OrderSource orderSource;

    private String storeId;

    private String loyaltyMemberId;

    private List<LineItem> baristaLineItems;

    private List<LineItem> kitchenLineItems;

    private Instant timestamp;

    public PlaceOrderCommand() {
    }

    public PlaceOrderCommand(String id, OrderSource orderSource, String loyaltyMemberId, List<LineItem> baristaLineItems, List<LineItem> kitchenLineItems, Instant timestamp) {
        this.id = id;
        this.orderSource = orderSource;
        this.loyaltyMemberId = loyaltyMemberId;
        this.baristaLineItems = baristaLineItems;
        this.kitchenLineItems = kitchenLineItems;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PlaceOrderCommand.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("orderSource='" + orderSource + "'")
                .add("loyaltyMemberId='" + loyaltyMemberId + "'")
                .add("baristaLineItems=" + baristaLineItems)
                .add("kitchenLineItems=" + kitchenLineItems)
                .add("timestamp=" + timestamp)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaceOrderCommand that = (PlaceOrderCommand) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (orderSource != null ? !orderSource.equals(that.orderSource) : that.orderSource != null) return false;
        if (loyaltyMemberId != null ? !loyaltyMemberId.equals(that.loyaltyMemberId) : that.loyaltyMemberId != null)
            return false;
        if (baristaLineItems != null ? !baristaLineItems.equals(that.baristaLineItems) : that.baristaLineItems != null)
            return false;
        if (kitchenLineItems != null ? !kitchenLineItems.equals(that.kitchenLineItems) : that.kitchenLineItems != null)
            return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (orderSource != null ? orderSource.hashCode() : 0);
        result = 31 * result + (loyaltyMemberId != null ? loyaltyMemberId.hashCode() : 0);
        result = 31 * result + (baristaLineItems != null ? baristaLineItems.hashCode() : 0);
        result = 31 * result + (kitchenLineItems != null ? kitchenLineItems.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
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

    public Optional<String> getLoyaltyMemberId() {
        return Optional.ofNullable(loyaltyMemberId);
    }

    public void setLoyaltyMemberId(String loyaltyMemberId) {
        this.loyaltyMemberId = loyaltyMemberId;
    }

    public Optional<List<LineItem>> getBaristaLineItems() {
        return Optional.ofNullable(baristaLineItems);
    }

    public void setBaristaLineItems(List<LineItem> baristaLineItems) {
        this.baristaLineItems = baristaLineItems;
    }

    public Optional<List<LineItem>> getKitchenLineItems() {
        return Optional.ofNullable(kitchenLineItems);
    }

    public void setKitchenLineItems(List<LineItem> kitchenLineItems) {
        this.kitchenLineItems = kitchenLineItems;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
