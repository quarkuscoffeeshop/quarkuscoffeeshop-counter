package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkuscoffeeshop.infrastructure.KafkaIT;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.any;

public abstract class BaseOrderIT extends KafkaIT {

    @InjectMock
    OrderRepository orderRepository;

    @InjectMock
    CoffeeshopEventRepository coffeeshopEventRepository;

    @InjectMock
    CoffeeShopCommandRepository coffeeShopCommandRepository;


    @BeforeEach
    public void setup() {
/*
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(orderRepository).persist(any(Order.class));
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(coffeeshopEventRepository).persist(any(OrderEvent.class));
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(coffeeShopCommandRepository).persist(any(CoffeeshopCommand.class));
*/
    }

}
