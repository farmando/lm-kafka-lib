package br.com.leroy.merlin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
  @NestedConfigurationProperty
  private KafkaProducerProperties producer = new KafkaProducerProperties();
  @NestedConfigurationProperty
  private KafkaConsumerProperties consumer = new KafkaConsumerProperties();
  @NestedConfigurationProperty
  private KafkaHealthProperties health = new KafkaHealthProperties();

  @Data
  public static class KafkaHealthProperties {
    private static final int DEFAULT_TIMEOUT = 10000;
    /**
     * Whether to enable Kafka health indicators.
     */
    private boolean enabled = true;

    /**
     * Timeout in milliseconds for health check operations.
     */
    private int timeout = DEFAULT_TIMEOUT;
  }
}
