package io.quarkuscoffeeshop.counter.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkuscoffeeshop.domain.*;
import io.quarkuscoffeeshop.infrastructure.CafeITResource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(CafeITResource.class)
public class KafkaServiceBaristaAndKitchenOrderIT extends BaseOrderIT {

    final List<OrderLineItem> baristaItems = Arrays.asList(
            new OrderLineItem(Item.COFFEE_WITH_ROOM, BigDecimal.valueOf(3.5), "Kirk"),
            new OrderLineItem(Item.ESPRESSO_DOUBLE, BigDecimal.valueOf(5.5), "Spock")
    );

    final List<OrderLineItem> kitchenItems = Arrays.asList(
            new OrderLineItem(Item.CAKEPOP, BigDecimal.valueOf(3.50), "Mickey"),
            new OrderLineItem(Item.MUFFIN, BigDecimal.valueOf(3.50),"Goofy")
    );

    @Test
    public void testOrderInBeveragesAndKitchen() throws InterruptedException {

        final PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(OrderSource.WEB, "testStoreId",null, baristaItems, kitchenItems, BigDecimal.valueOf(7.50));

        // send the order to Kafka
        producerMap.get("orders").send(new ProducerRecord("orders", JsonUtil.toJson(placeOrderCommand)));
        Thread.sleep(1000);


        // Get the appropriate consumer, point to the first message, and pull all messages
        final KafkaConsumer ordersConsumer = consumerMap.get("orders");
        ordersConsumer.seekToBeginning(new ArrayList<TopicPartition>());

        final ConsumerRecords<String, String> records = ordersConsumer.poll(Duration.ofMillis(1000));

        int baristaOrders = 0;
        int kitchenOrders = 0;

        // verify that the records are of the correct type
        Iterator iterator = records.iterator();
        while (iterator.hasNext()) {
            ConsumerRecord consumerRecord = (ConsumerRecord) iterator.next();
            String payload = (String) consumerRecord.value();
            JsonReader jsonReader = Json.createReader(new StringReader(payload));
            JsonObject jsonObject = jsonReader.readObject();

            // filter on the type of event
            if(jsonObject.containsKey("eventType")) {
                final OrderInEvent orderInEvent = JsonUtil.jsonb.fromJson(payload, OrderInEvent.class);
                assertNotNull(orderInEvent.eventType);
                if(orderInEvent.eventType.equals(EventType.BEVERAGE_ORDER_IN)){
                    assertTrue(orderInEvent.item.equals(Item.ESPRESSO_DOUBLE) || orderInEvent.item.equals(Item.COFFEE_WITH_ROOM),
                            "The item should be either a " + Item.ESPRESSO_DOUBLE + " or a " + Item.COFFEE_WITH_ROOM + " not a " + orderInEvent.item);
                    baristaOrders++;
                } else if (orderInEvent.eventType.equals(EventType.KITCHEN_ORDER_IN)) {
                    assertTrue(orderInEvent.item.equals(Item.CAKEPOP) || orderInEvent.item.equals(Item.MUFFIN),
                            "The item should be either a " + Item.MUFFIN + " or a " + Item.CAKEPOP + " not a " + orderInEvent.item);
                    kitchenOrders++;
                }
            }
        };

        assertEquals(2, baristaOrders);
        assertEquals(2, kitchenOrders);
    }

    @Test
    public void testOrderInBeveragesOnly() throws InterruptedException {

        final PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(OrderSource.WEB,  "testStoreId", null, baristaItems, null, BigDecimal.valueOf(7.50));

        // send the order to Kafka and wait
        producerMap.get("orders").send(new ProducerRecord("orders", JsonUtil.jsonb.toJson(placeOrderCommand)));
        Thread.sleep(1000);

        final KafkaConsumer ordersConsumer = consumerMap.get("orders");
        ordersConsumer.seekToBeginning(new ArrayList<TopicPartition>());

        final ConsumerRecords<String, String> records = ordersConsumer.poll(Duration.ofMillis(1000));

        int baristaOrders = 0;

        // verify that the records are of the correct type
        Iterator iterator = records.iterator();
        while (iterator.hasNext()) {
            ConsumerRecord consumerRecord = (ConsumerRecord) iterator.next();
            String payload = (String) consumerRecord.value();
            JsonReader jsonReader = Json.createReader(new StringReader(payload));
            JsonObject jsonObject = jsonReader.readObject();

            // filter on the type of event
            if(jsonObject.containsKey("eventType")) {
                final OrderInEvent orderInEvent = JsonUtil.jsonb.fromJson(payload, OrderInEvent.class);
                assertEquals(EventType.BEVERAGE_ORDER_IN, orderInEvent.eventType);
                assertTrue(orderInEvent.item.equals(Item.ESPRESSO_DOUBLE) || orderInEvent.item.equals(Item.COFFEE_WITH_ROOM),
                        "The item should be either a " + Item.ESPRESSO_DOUBLE + " or a " + Item.COFFEE_WITH_ROOM + " not a " + orderInEvent.item);
                    baristaOrders++;
            }
        };

        assertEquals(2, baristaOrders);
    }

    @Test
    public void testOrderInKitchenOnly() throws InterruptedException{
        final PlaceOrderCommand placeOrderCommand = new PlaceOrderCommand(OrderSource.WEB,  "testStoreId",null, null, kitchenItems, BigDecimal.valueOf(7.50));

        // send the order to Kafka and wait
        producerMap.get("orders").send(new ProducerRecord("orders", JsonUtil.jsonb.toJson(placeOrderCommand)));
        Thread.sleep(1000);

        final KafkaConsumer ordersConsumer = consumerMap.get("orders");
        ordersConsumer.seekToBeginning(new ArrayList<TopicPartition>());

        final ConsumerRecords<String, String> records = ordersConsumer.poll(Duration.ofMillis(1000));

        int kitchenOrders = 0;

        // verify that the records are of the correct type
        Iterator iterator = records.iterator();
        while (iterator.hasNext()) {
            ConsumerRecord consumerRecord = (ConsumerRecord) iterator.next();
            String payload = (String) consumerRecord.value();
            JsonReader jsonReader = Json.createReader(new StringReader(payload));
            JsonObject jsonObject = jsonReader.readObject();

            // filter on the type of event
            if(jsonObject.containsKey("eventType")) {
                final OrderInEvent orderInEvent = JsonUtil.jsonb.fromJson(payload, OrderInEvent.class);
                assertEquals(EventType.KITCHEN_ORDER_IN, orderInEvent.eventType);
                assertTrue(orderInEvent.item.equals(Item.CAKEPOP) || orderInEvent.item.equals(Item.MUFFIN),
                        "The item should be either a " + Item.MUFFIN + " or a " + Item.CAKEPOP + " not a " + orderInEvent.item);
                kitchenOrders++;
            }
        };

        assertEquals(2, kitchenOrders);
    }
}
