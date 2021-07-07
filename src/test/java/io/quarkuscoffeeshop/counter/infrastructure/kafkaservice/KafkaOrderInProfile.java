package io.quarkuscoffeeshop.counter.infrastructure.kafkaservice;

import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkuscoffeeshop.counter.infrastructure.KafkaTestResource;
import io.quarkuscoffeeshop.counter.infrastructure.OrderServiceMock;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * This profile assures that a new instance of Quarkus will be started for any tests with the annotation
 */
public class KafkaOrderInProfile implements QuarkusTestProfile {

    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Collections.singleton(OrderServiceMock.class);
    }

    @Override
    public List<TestResourceEntry> testResources() {
        return Collections.singletonList(new TestResourceEntry(KafkaTestResource.class));
    }
}
