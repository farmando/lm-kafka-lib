package br.com.leroy.merlin.producer;

import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaPublisherFactory implements PublisherFactory {
  private final KafkaOperations<Object, Object> kafkaOperations;

  public KafkaPublisherFactory(KafkaOperations<Object, Object> kafkaOperations) {
    this.kafkaOperations = kafkaOperations;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K, V> Publisher<K, V> createPublisher(Class<K> keyClass, Class<V> valueClass) {
    return new KafkaPublisher<>((KafkaTemplate<K, V>) kafkaOperations);
  }
}
