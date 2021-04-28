package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.test.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@Mock
@ApplicationScoped @Transactional
public class OrderRepositoryMock extends OrderRepository{

    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryMock.class);

    @Override
    public void persist(Order order) {
        logger.debug("mocking persist for {}", order);
    }
}
