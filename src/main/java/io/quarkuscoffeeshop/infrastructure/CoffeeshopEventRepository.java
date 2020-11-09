package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkuscoffeeshop.counter.domain.OrderEvent;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoffeeshopEventRepository  implements PanacheRepository<OrderEvent> {
}
