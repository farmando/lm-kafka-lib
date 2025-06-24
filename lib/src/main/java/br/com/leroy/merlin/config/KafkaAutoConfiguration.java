package br.com.leroy.merlin.config;

import br.com.leroy.merlin.health.KafkaHealthConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(
    prefix = "kafka",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@Import({
    KafkaProducerConfiguration.class,
    KafkaConsumerConfiguration.class,
    KafkaHealthConfiguration.class
})
public class KafkaAutoConfiguration {
}
