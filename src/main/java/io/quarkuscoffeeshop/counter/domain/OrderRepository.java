package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<OrderRecord> {

    public Order findById(final String orderId) {
<<<<<<< HEAD

        return Order.findById(orderId);
=======
        OrderRecord orderRecord = OrderRecord.findById(orderId);
        return Order.fromOrderRecord(orderRecord);
>>>>>>> c06077d8b2fd9c4db6c101e00c11ad3b879eb629
    }

    public void persist(Order order) {
        this.persist(order.getOrderRecord());
    }

}
