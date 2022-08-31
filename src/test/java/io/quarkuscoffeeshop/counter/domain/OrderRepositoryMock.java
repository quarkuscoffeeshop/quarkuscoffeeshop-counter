package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.test.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.transaction.Transactional;

@Alternative
@ApplicationScoped
public class OrderRepositoryMock extends OrderRepository{

    private static final Logger logger = LoggerFactory.getLogger(OrderRepositoryMock.class);

    @Override
    public void persist(OrderRecord orderRecord) {
        logger.debug("mocking persist for {}", orderRecord);
    }
}
