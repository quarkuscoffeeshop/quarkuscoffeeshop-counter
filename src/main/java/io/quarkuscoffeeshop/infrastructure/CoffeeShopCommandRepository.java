package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopCommand;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CoffeeShopCommandRepository implements PanacheRepository<CoffeeshopCommand> {
}
