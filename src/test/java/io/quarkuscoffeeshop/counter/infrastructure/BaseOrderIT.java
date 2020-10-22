package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopCommand;
import io.quarkuscoffeeshop.counter.domain.CoffeeshopEvent;
import io.quarkuscoffeeshop.counter.domain.Order;
import io.quarkuscoffeeshop.infrastructure.CoffeeShopCommandRepository;
import io.quarkuscoffeeshop.infrastructure.CoffeeshopEventRepository;
import io.quarkuscoffeeshop.infrastructure.KafkaIT;
import io.quarkuscoffeeshop.infrastructure.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.util.UUID;

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
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(orderRepository).persist(any(Order.class));
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(coffeeshopEventRepository).persist(any(CoffeeshopEvent.class));
        Mockito.doAnswer(new TestUtil.AssignIdToEntityAnswer(UUID.randomUUID().toString())).when(coffeeShopCommandRepository).persist(any(CoffeeshopCommand.class));
    }

}
