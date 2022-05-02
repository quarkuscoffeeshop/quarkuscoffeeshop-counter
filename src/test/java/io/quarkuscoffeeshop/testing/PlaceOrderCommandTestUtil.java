package io.quarkuscoffeeshop.testing;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkuscoffeeshop.counter.domain.Item;
import io.quarkuscoffeeshop.counter.domain.Location;
import io.quarkuscoffeeshop.counter.domain.OrderSource;
import io.quarkuscoffeeshop.counter.domain.commands.CommandItem;
import io.quarkuscoffeeshop.counter.domain.commands.PlaceOrderCommand;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlaceOrderCommandTestUtil {

    private String id;

    private OrderSource orderSource;

    private Location location;

    private String loyaltyMemberId;

    private List<CommandItem> baristaLineItems;

    private List<CommandItem> kitchenLineItems;

    private Instant timestamp;

    public PlaceOrderCommandTestUtil() {
        this.id = UUID.randomUUID().toString();
    }

    public PlaceOrderCommandTestUtil(String id, OrderSource orderSource, Location location, String loyaltyMemberId, List<CommandItem> baristaLineItems, List<CommandItem> kitchenLineItems, Instant timestamp) {
        this.id = id;
        this.orderSource = orderSource;
        this.location = location;
        this.loyaltyMemberId = loyaltyMemberId;
        this.baristaLineItems = baristaLineItems;
        this.kitchenLineItems = kitchenLineItems;
        this.timestamp = timestamp;
    }

    public PlaceOrderCommandTestUtil create() {
        this.id = UUID.randomUUID().toString();
        return this;
    }

    public void withId(final String id) {
        this.id = id;
    }

    public PlaceOrderCommandTestUtil withBlackCoffee() {
        if (this.baristaLineItems == null) {
            this.baristaLineItems = new ArrayList<>();
        }
        this.baristaLineItems.add(new CommandItem(Item.COFFEE_BLACK, "Jerry", BigDecimal.valueOf(3.50)));
        return this;
    }

    public void withBlackCoffeeFor(final String name) {
        this.baristaLineItems.add(new CommandItem(Item.COFFEE_BLACK, name, BigDecimal.valueOf(3.50)));
    }

    public PlaceOrderCommand build() {
        return new PlaceOrderCommand(
            this.id,
            this.orderSource,
            this.location,
            this.loyaltyMemberId,
            Optional.ofNullable(this.baristaLineItems),
            Optional.ofNullable(this.kitchenLineItems)
        );
    }
}
