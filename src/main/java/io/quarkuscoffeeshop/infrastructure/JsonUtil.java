package io.quarkuscoffeeshop.infrastructure;

import io.quarkuscoffeeshop.domain.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@RegisterForReflection
public class JsonUtil {

    static Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public static final Jsonb jsonb = JsonbBuilder.create();

    public static String toJson(Object object) {
        return jsonb.toJson(object);
    }

    public static String toDashboardUpdateReadyJson(String payload) {
        logger.debug("converting payload to OrderReadyUpdate: {}", payload);
        OrderUpEvent orderUpEvent = jsonb.fromJson(payload, OrderUpEvent.class);
        return jsonb.toJson(new OrderReadyUpdate(orderUpEvent));
    }

    public static OrderPlacedEvent createOrderPlacedEventFromJson(String payload) {
        return jsonb.fromJson(payload, OrderPlacedEvent.class);
    }

    public static String toInProgressUpdate(final LineItemEvent lineItemEvent) {
        return jsonb.toJson(new InQueueUpdate(lineItemEvent));
    }

}
