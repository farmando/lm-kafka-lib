package br.com.leroy.merlin.producer;

import br.com.leroy.merlin.config.StringPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Qualifier("stringPublisher")
@ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
    havingValue = "org.apache.kafka.common.serialization.StringSerializer", matchIfMissing = true)
public class StringKafkaPublisher extends KafkaPublisher<String, String> implements StringPublisher {

  public StringKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
    super(kafkaTemplate);
  }
}
