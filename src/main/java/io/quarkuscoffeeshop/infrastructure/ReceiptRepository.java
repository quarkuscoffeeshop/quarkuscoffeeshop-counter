package io.quarkuscoffeeshop.infrastructure;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkuscoffeeshop.counter.domain.Receipt;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReceiptRepository implements PanacheRepository<Receipt> {
}
