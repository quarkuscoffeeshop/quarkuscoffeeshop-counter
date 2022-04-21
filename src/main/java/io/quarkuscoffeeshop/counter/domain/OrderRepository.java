package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<OrderRecord> {

    public Order findById(final String orderId) {
        OrderRecord orderRecord = OrderRecord.findById(orderId);
        return Order.fromOrderRecord(orderRecord);
    }

    public void persist(Order order) {
        this.persist(order.getOrderRecord());
    }

}
