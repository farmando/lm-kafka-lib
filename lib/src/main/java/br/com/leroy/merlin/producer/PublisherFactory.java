package br.com.leroy.merlin.producer;

public interface PublisherFactory {
  <K, V> Publisher<K, V> createPublisher(Class<K> keyClass, Class<V> valueClass);
}
