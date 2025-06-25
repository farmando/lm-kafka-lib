package br.com.leroy.merlin.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import br.com.leroy.merlin.config.KafkaConsumerProperties;
import br.com.leroy.merlin.config.KafkaProducerProperties;
import br.com.leroy.merlin.config.KafkaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class KafkaHealthIndicatorTest {
  @Mock
  private KafkaProperties kafkaProperties;
  @Mock
  private KafkaProducerProperties producerProperties;
  @Mock
  private KafkaConsumerProperties consumerProperties;
  private KafkaHealthIndicator kafkaHealthIndicator;
  private static final int TIMEOUT = 100;

  @BeforeEach
  void setUp() {
    kafkaHealthIndicator = new KafkaHealthIndicator(kafkaProperties, TIMEOUT);
  }

  @Test
  void shouldReturnUnknownWhenNoBootstrapServersConfigured() {
    // given
    when(kafkaProperties.getProducer()).thenReturn(null);
    when(kafkaProperties.getConsumer()).thenReturn(null);

    // when
    Health health = kafkaHealthIndicator.health();

    // then
    assertEquals(Status.UNKNOWN, health.getStatus());
    assertEquals("Kafka bootstrap servers not configured", health.getDetails().get("message"));
  }

  @Test
  void shouldUseProducerBootstrapServersWhenAvailable() {
    // given - this test will return DOWN as we can't mock the AdminClient easily
    when(kafkaProperties.getProducer()).thenReturn(producerProperties);
    when(producerProperties.getBootstrapServers()).thenReturn("localhost:9092");

    // when
    Health health = kafkaHealthIndicator.health();

    // then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("localhost:9092", health.getDetails().get("bootstrapServers"));
  }

  @Test
  void shouldUseConsumerBootstrapServersWhenProducerNotAvailable() {
    // given - this test will return DOWN as we can't mock the AdminClient easily
    when(kafkaProperties.getProducer()).thenReturn(null);
    when(kafkaProperties.getConsumer()).thenReturn(consumerProperties);
    when(consumerProperties.getBootstrapServers()).thenReturn("localhost:9092");

    // when
    Health health = kafkaHealthIndicator.health();

    // then
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("localhost:9092", health.getDetails().get("bootstrapServers"));
  }
}