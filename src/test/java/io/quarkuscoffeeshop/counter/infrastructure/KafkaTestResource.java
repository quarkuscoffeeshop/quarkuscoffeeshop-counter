package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;

import java.util.HashMap;
import java.util.Map;

/**
 * Switches Kafka Topics to in-memory channels for testing
 */
public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props1 = InMemoryConnector.switchIncomingChannelsToInMemory("orders-in");
        Map<String, String> props2 = InMemoryConnector.switchIncomingChannelsToInMemory("orders-up");
        Map<String, String> props3 = InMemoryConnector.switchOutgoingChannelsToInMemory("barista-in");
        Map<String, String> props4 = InMemoryConnector.switchOutgoingChannelsToInMemory("kitchen-in");
        Map<String, String> props5 = InMemoryConnector.switchOutgoingChannelsToInMemory("web-updates");
        env.putAll(props1);
        env.putAll(props2);
        env.putAll(props3);
        env.putAll(props4);
        env.putAll(props5);
        return env;
    }

    @Override
    public void stop() {

        InMemoryConnector.clear();
    }
}
