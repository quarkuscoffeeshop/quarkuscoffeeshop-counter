package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkuscoffeeshop.counter.infrastructure.KafkaTestResource;
import io.quarkuscoffeeshop.testing.DBTestProfile;
import io.quarkuscoffeeshop.testing.TestUtil;
import org.junit.jupiter.api.BeforeAll;

import javax.inject.Inject;
import javax.transaction.Transactional;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class) @Transactional
@TestProfile(DBTestProfile.class)
public class LineItemStatusTest {

    @Inject
    OrderRepository orderRepository;

    String orderId;

    @BeforeAll
    public void setUp() {
        Order order = TestUtil.stubOrder();
        orderId = order.getOrderId();
        orderRepository.persist(order);
    }


}
