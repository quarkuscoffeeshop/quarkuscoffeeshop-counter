package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopCommand;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopEvent;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoffeeShopCommandRepository implements PanacheMongoRepository<CoffeeshopCommand> {
}
