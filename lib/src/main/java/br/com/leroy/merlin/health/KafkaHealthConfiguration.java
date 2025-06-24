package br.com.leroy.merlin.health;

import br.com.leroy.merlin.config.KafkaProperties;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
@ConditionalOnProperty(prefix = "kafka.health", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaHealthConfiguration {

  @Bean
  @ConditionalOnBean(KafkaProperties.class)
  public HealthIndicator kafkaHealthIndicator(KafkaProperties kafkaProperties) {
    return new KafkaHealthIndicator(
        kafkaProperties,
        kafkaProperties.getHealth().getTimeout()
    );
  }
}
