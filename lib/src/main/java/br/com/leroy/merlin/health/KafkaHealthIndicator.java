package br.com.leroy.merlin.health;

import br.com.leroy.merlin.config.CommonKafkaProperties;
import br.com.leroy.merlin.config.KafkaProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.util.StringUtils;

@Slf4j
public class KafkaHealthIndicator implements HealthIndicator {

  private static final String BOOTSTRAP_SERVERS_CONFIG_KEY = "bootstrapServers";
  private final KafkaProperties kafkaProperties;
  private final int timeout;

  public KafkaHealthIndicator(KafkaProperties kafkaProperties, int timeout) {
    this.kafkaProperties = kafkaProperties;
    this.timeout = timeout;
  }

  @Override
  public Health health() {
    AdminClient adminClient = null;
    try {
      String bootstrapServers = getBootstrapServers();

      if (!StringUtils.hasText(bootstrapServers)) {
        return Health.unknown()
            .withDetail("message", "Kafka bootstrap servers not configured")
            .build();
      }

      adminClient = AdminClient.create(createAdminClientConfig(bootstrapServers));

      ListTopicsOptions options = new ListTopicsOptions().timeoutMs(timeout);
      adminClient.listTopics(options).names().get(timeout, TimeUnit.MILLISECONDS);

      return Health.up()
          .withDetail(BOOTSTRAP_SERVERS_CONFIG_KEY, bootstrapServers)
          .build();

    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("Kafka health check interrupted: {}", e.getMessage());
      return Health.down()
          .withDetail(BOOTSTRAP_SERVERS_CONFIG_KEY, getBootstrapServers())
          .withDetail("error", "Thread interrupted: " + e.getMessage())
          .build();
    } catch (Exception e) {
      log.warn("Kafka health check failed: {}", e.getMessage());
      return Health.down()
          .withDetail(BOOTSTRAP_SERVERS_CONFIG_KEY, getBootstrapServers())
          .withDetail("error", e.getClass().getSimpleName() + ": " + e.getMessage())
          .build();
    } finally {
      if (adminClient != null) {
        try {
          adminClient.close();
        } catch (Exception e) {
          log.warn("Error closing Kafka admin client: {}", e.getMessage());
        }
      }
    }
  }

  private String getBootstrapServers() {
    String producerServers = kafkaProperties.getProducer() != null
        ? kafkaProperties.getProducer().getBootstrapServers()
        : null;

    if (StringUtils.hasText(producerServers)) {
      return producerServers;
    }

    return kafkaProperties.getConsumer() != null
        ? kafkaProperties.getConsumer().getBootstrapServers()
        : null;
  }

  private Map<String, Object> createAdminClientConfig(String bootstrapServers) {
    Map<String, Object> config = new HashMap<>();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, timeout);
    config.put(AdminClientConfig.RETRIES_CONFIG, 2);

    if (kafkaProperties.getProducer() != null) {
      CommonKafkaProperties producer = kafkaProperties.getProducer();

      if (StringUtils.hasText(producer.getSecurityProtocol())) {
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
            producer.getSecurityProtocol());
      }
      if (StringUtils.hasText(producer.getSaslMechanism())) {
        config.put(SaslConfigs.SASL_MECHANISM,
            producer.getSaslMechanism());
      }
      if (StringUtils.hasText(producer.getSaslJaasConfig())) {
        config.put(SaslConfigs.SASL_JAAS_CONFIG,
            producer.getSaslJaasConfig());
      }
      if (StringUtils.hasText(producer.getSchemaRegistryUrl())) {
        config.put("schema.registry.url",
            producer.getSchemaRegistryUrl());
      }
    }

    return config;
  }
}
