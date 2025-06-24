package br.com.leroy.merlin.producer;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("stringPublisher")
@ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
    havingValue = "org.apache.kafka.common.serialization.StringSerializer", matchIfMissing = true)
public class AvroKafkaPublisher<V> extends KafkaPublisher<String, V> implements AvroPublisher<V> {

  public AvroKafkaPublisher(KafkaTemplate<String, V> kafkaTemplate) {
    super(kafkaTemplate);
  }
}

