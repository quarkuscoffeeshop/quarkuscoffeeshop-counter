package io.quarkuscoffeeshop.counter.domain;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.quarkuscoffeeshop.counter.infrastructure.KafkaTestResource;
import io.quarkuscoffeeshop.testing.DBTestProfile;

import javax.transaction.Transactional;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class) @Transactional
@TestProfile(DBTestProfile.class)
public class LineItemStatusTest {
}
