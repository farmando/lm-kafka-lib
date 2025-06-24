package br.com.leroy.merlin.producer;

import java.util.concurrent.CompletableFuture;

public interface Publisher<K, V> {
  CompletableFuture<Void> publish(String topic, GenericMessage<K, V> message);

  default CompletableFuture<Void> publish(String topic, V value) {
    return publish(topic, new GenericMessage<>(null, value));
  }

  default CompletableFuture<Void> publish(String topic, K key, V value) {
    return publish(topic, new GenericMessage<>(key, value));
  }
}
