package br.com.leroy.merlin.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kafka.consumer", name = "bootstrap-servers")
public class KafkaConsumerConfiguration {
  private final KafkaProperties kafkaProperties;

  @Bean
  public ConsumerFactory<Object, Object> consumerFactory(
      @Autowired(required = false) List<KafkaConsumerConfigCustomizer> customizers) {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(customizers));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
      ConsumerFactory<Object, Object> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(kafkaProperties.getConsumer().getConcurrency());
    return factory;
  }

  @Bean
  public Map<String, Object> consumerConfigs(
      @Autowired(required = false) List<KafkaConsumerConfigCustomizer> customizers) {
    Map<String, Object> props = new HashMap<>();
    KafkaConsumerProperties consumer = kafkaProperties.getConsumer();

    configureBasicProperties(props, consumer);
    configureConsumerBehavior(props, consumer);
    KafkaConfigurationHelper.configureSecurityProperties(props, consumer);
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, consumer);

    if (customizers != null) {
      customizers.forEach(customizer -> customizer.customize(props));
    }

    return props;
  }

  private void configureBasicProperties(Map<String, Object> props, KafkaConsumerProperties consumer) {
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumer.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumer.getKeyDeserializer());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumer.getValueDeserializer());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
  }

  private void configureConsumerBehavior(Map<String, Object> props, KafkaConsumerProperties consumer) {
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset());
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.getEnableAutoCommit());
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumer.getMaxPollRecords());
  }
}
