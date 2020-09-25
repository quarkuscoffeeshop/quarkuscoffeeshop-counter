package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopEvent;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoffeeshopEventRepository  implements PanacheMongoRepository<CoffeeshopEvent> {
}
