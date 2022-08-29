package io.quarkuscoffeeshop.counter.domain.valueobjects;

import io.quarkuscoffeeshop.counter.domain.LineItem;

import java.util.List;

public record OrderRecord(String orderId, List<LineItem> lineItemList) {
}
