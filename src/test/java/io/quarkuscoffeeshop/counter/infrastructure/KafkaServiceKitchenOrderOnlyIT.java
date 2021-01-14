package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.infrastructure.CafeITResource;
import io.vertx.kafka.client.common.TopicPartition;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(CafeITResource.class)
public class KafkaServiceKitchenOrderOnlyIT extends BaseOrderIT {

    @Test
    public void testOrderInKitchenOnly() throws InterruptedException{

        final List<OrderLineItem> kitchenItems = new ArrayList<>();
        kitchenItems.add(new OrderLineItem(Item.CAKEPOP, BigDecimal.valueOf(3.50), "Mickey"));
        kitchenItems.add(new OrderLineItem(Item.MUFFIN, BigDecimal.valueOf(3.50),"Goofy"));
        final PlaceOrderCommand createOrderCommand = new PlaceOrderCommand(OrderSource.WEB, "testStoreId", null, null, kitchenItems, BigDecimal.valueOf(7.50));

        // send the order to Kafka
        producerMap.get("web-in").send(new ProducerRecord("web-in", jsonb.toJson(createOrderCommand)));

        Thread.sleep(2000);

        // Get the appropriate consumer, point to the first message, and pull all messages
        final KafkaConsumer kitchenConsumer = consumerMap.get("kitchen-in");
        kitchenConsumer.seekToBeginning(new ArrayList<TopicPartition>()); //
        final ConsumerRecords<String, String> newRecords = kitchenConsumer.poll(Duration.ofMillis(5000));

        // verify that the records are of the correct type
        newRecords.forEach(record -> {
            System.out.println(record.value());
            final OrderInEvent orderInEvent = JsonUtil.jsonb.fromJson(record.value(), OrderInEvent.class);
            assertEquals(EventType.KITCHEN_ORDER_IN, orderInEvent.eventType);
            assertTrue(orderInEvent.item.equals(Item.CAKEPOP) || orderInEvent.item.equals(Item.MUFFIN),
                    "The item should be either a " + Item.MUFFIN + " or a " + Item.CAKEPOP + " not a " + orderInEvent.item);
        });

    }
}
