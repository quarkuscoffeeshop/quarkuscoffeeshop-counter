package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.infrastructure.CafeITResource;
import io.quarkuscoffeeshop.infrastructure.JsonUtil;
import io.vertx.kafka.client.common.TopicPartition;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(CafeITResource.class)
public class KafkaServiceBaristaOrderOnlyIT extends BaseOrderIT {
    
    @Test
    public void testOrderInBeveragesOnly() throws InterruptedException {

        final List<OrderLineItem> beverages = new ArrayList<>();
        beverages.add(new OrderLineItem(Item.COFFEE_WITH_ROOM, BigDecimal.valueOf(3.5), "Kirk"));
        beverages.add(new OrderLineItem(Item.ESPRESSO_DOUBLE, BigDecimal.valueOf(5.5), "Spock"));
        final PlaceOrderCommand createOrderCommand = new PlaceOrderCommand(OrderSource.WEB, "testStoreId", null, beverages, null, BigDecimal.valueOf(7.50));

        // send the order to Kafka and wait
        producerMap.get("web-in").send(new ProducerRecord("web-in", jsonb.toJson(createOrderCommand)));
        Thread.sleep(1000);

        // intercept the messages from the appropriate consumer
        final KafkaConsumer baristaConsumer = consumerMap.get("barista-in");
        baristaConsumer.seekToBeginning(new ArrayList<TopicPartition>()); //
        final ConsumerRecords<String, String> newRecords = baristaConsumer.poll(Duration.ofMillis(2000));

        // verify that the records are of the correct type
        newRecords.forEach(record -> {
            System.out.println("baristaOrder: " + record.value());
            final OrderInEvent orderInEvent = JsonUtil.jsonb.fromJson(record.value(), OrderInEvent.class);
            assertTrue(orderInEvent.item.equals(Item.ESPRESSO_DOUBLE) || orderInEvent.item.equals(Item.COFFEE_WITH_ROOM),
                    "The item should be either a " + Item.ESPRESSO_DOUBLE + " or a " + Item.COFFEE_WITH_ROOM + " not a " + orderInEvent.item);
        });

        // verify the number of new records
        //assertEquals(2, newRecords.count());
    }
}