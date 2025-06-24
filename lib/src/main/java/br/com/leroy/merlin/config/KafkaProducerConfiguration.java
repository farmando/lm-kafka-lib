package br.com.leroy.merlin.config;

import br.com.leroy.merlin.producer.AvroPublisher;
import br.com.leroy.merlin.producer.KafkaPublisher;
import br.com.leroy.merlin.producer.KafkaPublisherFactory;
import br.com.leroy.merlin.producer.PublisherFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kafka.producer", name = "bootstrap-servers")
public class KafkaProducerConfiguration {

  private final KafkaProperties kafkaProperties;

  @Bean
  public ProducerFactory<Object, Object> producerFactory(
      @Autowired(required = false) List<KafkaProducerConfigCustomizer> customizers) {
    return new DefaultKafkaProducerFactory<>(producerConfigs(customizers));
  }

  @Bean
  public KafkaTemplate<Object, Object> kafkaTemplate(
      ProducerFactory<Object, Object> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public PublisherFactory publisherFactory(KafkaTemplate<Object, Object> kafkaTemplate) {
    return new KafkaPublisherFactory(kafkaTemplate);
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "org.apache.kafka.common.serialization.StringSerializer", matchIfMissing = true)
  public ProducerFactory<String, String> stringProducerFactory(
      @Autowired(required = false) List<KafkaProducerConfigCustomizer> customizers) {
    return new DefaultKafkaProducerFactory<>(producerConfigs(customizers));
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "org.apache.kafka.common.serialization.StringSerializer", matchIfMissing = true)
  public KafkaTemplate<String, String> stringKafkaTemplate(
      ProducerFactory<String, String> stringProducerFactory) {
    return new KafkaTemplate<>(stringProducerFactory);
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "org.apache.kafka.common.serialization.StringSerializer", matchIfMissing = true)
  public StringPublisher stringPublisher(KafkaTemplate<String, String> stringKafkaTemplate) {
    return new StringKafkaPublisher(stringKafkaTemplate);
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "io.confluent.kafka.serializers.KafkaAvroSerializer")
  public <V> ProducerFactory<String, V> avroProducerFactory(
      @Autowired(required = false) List<KafkaProducerConfigCustomizer> customizers) {
    return new DefaultKafkaProducerFactory<>(producerConfigs(customizers));
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "io.confluent.kafka.serializers.KafkaAvroSerializer")
  public <V> KafkaTemplate<String, V> avroKafkaTemplate(
      ProducerFactory<String, V> avroProducerFactory) {
    return new KafkaTemplate<>(avroProducerFactory);
  }

  @Bean
  @ConditionalOnProperty(prefix = "kafka.producer", name = "value-serializer",
      havingValue = "io.confluent.kafka.serializers.KafkaAvroSerializer")
  public <V> AvroPublisher<V> avroPublisher(KafkaTemplate<String, V> avroKafkaTemplate) {
    return new AvroKafkaPublisher<>(avroKafkaTemplate);
  }

  @Bean
  public Map<String, Object> producerConfigs(
      @Autowired(required = false) List<KafkaProducerConfigCustomizer> customizers) {
    Map<String, Object> props = new HashMap<>();
    KafkaProducerProperties producer = kafkaProperties.getProducer();

    configureBasicProperties(props, producer);
    configurePerformanceProperties(props, producer);
    KafkaConfigurationHelper.configureSecurityProperties(props, producer);
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, producer);

    if (producer.getSchemaRegistryUrl() != null) {
      props.put("auto.register.schemas", producer.getAutoRegisterSchemas());

      KafkaSchemaStrategyEnum strategy = KafkaSchemaStrategyEnum.valueOf(
          producer.getSchemaStrategy());
      props.put("value.subject.name.strategy", strategy.getConfigValue());
    }

    if (customizers != null) {
      customizers.forEach(customizer -> customizer.customize(props));
    }

    return props;
  }

  private void configureBasicProperties(Map<String, Object> props,
      KafkaProducerProperties producer) {
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producer.getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producer.getKeySerializer());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producer.getValueSerializer());

    if (producer.getClientId() != null) {
      props.put(ProducerConfig.CLIENT_ID_CONFIG, producer.getClientId());
    }
  }

  private void configurePerformanceProperties(Map<String, Object> props,
      KafkaProducerProperties producer) {
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producer.getCompressionType());
    props.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
    props.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
    props.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producer.getRequestTimeoutMs());
    props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producer.getDeliveryTimeoutMs());
    props.put(ProducerConfig.ACKS_CONFIG, producer.getAcks());
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producer.getEnableIdempotence());
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
        producer.getMaxInFlightRequestsPerConnection());
  }

  public static class StringKafkaPublisher extends KafkaPublisher<String, String> implements
      StringPublisher {

    public StringKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
      super(kafkaTemplate);
    }
  }

  public static class AvroKafkaPublisher<V> extends KafkaPublisher<String, V> implements
      AvroPublisher<V> {

    public AvroKafkaPublisher(KafkaTemplate<String, V> kafkaTemplate) {
      super(kafkaTemplate);
    }
  }
}
