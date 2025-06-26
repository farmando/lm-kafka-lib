package br.com.leroy.merlin.producer;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public class KafkaPublisher<K, V> implements Publisher<K, V> {

  private final KafkaTemplate<K, V> kafkaTemplate;

  public KafkaPublisher(KafkaTemplate<K, V> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  @SuppressWarnings("java:S4449")
  public CompletableFuture<Void> publish(String topic, GenericMessage<K, V> message) {
    return kafkaTemplate.send(topic, message.key(), message.value())
        .thenAccept(sendResult -> {
          RecordMetadata metadata = sendResult.getRecordMetadata();

          String messageKey = Optional.ofNullable(message.key()).map(Objects::toString)
              .orElse("null");

          log.debug("Message sent successfully to topic={}, partition={}, offset={}, key={}",
              metadata.topic(),
              metadata.partition(),
              metadata.offset(),
              messageKey);
        })
        .exceptionally(throwable -> {
          log.error("Error sending message to topic {}: {}",
              topic, throwable.getMessage(), throwable);
          throw new CompletionException(new KafkaException(
              "Failed to send message to Kafka", throwable));
        });
  }
}
