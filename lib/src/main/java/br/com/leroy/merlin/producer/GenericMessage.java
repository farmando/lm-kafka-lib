package br.com.leroy.merlin.producer;

import io.micrometer.common.lang.Nullable;

public record GenericMessage<K, V>(@Nullable K key, V value) {

  public static <K, V> GenericMessage<K, V> of(K key, V value) {
    return new GenericMessage<>(key, value);
  }

  public static <V> GenericMessage<String, V> withStringKey(String key, V value) {
    return new GenericMessage<>(key, value);
  }

  public static <V> GenericMessage<String, V> withNullKey(V value) {
    return new GenericMessage<>(null, value);
  }
}
