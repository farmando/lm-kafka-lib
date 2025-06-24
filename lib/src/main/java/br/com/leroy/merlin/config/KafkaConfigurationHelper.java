package br.com.leroy.merlin.config;

import static java.util.Optional.ofNullable;

import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

public class KafkaConfigurationHelper {

  private KafkaConfigurationHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static void configureSecurityProperties(Map<String, Object> props,
      CommonKafkaProperties properties) {
    if (properties.getSecurityProtocol() == null) {
      return;
    }

    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, properties.getSecurityProtocol());

    if ("PLAINTEXT".equals(properties.getSecurityProtocol())) {
      return;
    }

    ofNullable(properties.getSaslMechanism())
        .ifPresent(mechanism -> props.put(SaslConfigs.SASL_MECHANISM, mechanism));

    ofNullable(properties.getSaslJaasConfig())
        .ifPresent(config -> props.put(SaslConfigs.SASL_JAAS_CONFIG, config));

    ofNullable(properties.getSslEndpointIdentificationAlgorithm())
        .ifPresent(algorithm -> props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG,
            algorithm));
  }

  public static void configureSchemaRegistryProperties(Map<String, Object> props,
      CommonKafkaProperties properties) {
    if (properties.getSchemaRegistryUrl() != null) {
      props.put("schema.registry.url", properties.getSchemaRegistryUrl());
      configureSchemaRegistryAuth(props, properties);
    }
  }

  public static void configureSchemaRegistryAuth(Map<String, Object> props,
      CommonKafkaProperties properties) {
    if (properties.getBasicAuthUserInfo() != null) {
      props.put("basic.auth.credentials.source", properties.getBasicAuthCredentialsSource() != null
          ? properties.getBasicAuthCredentialsSource()
          : "USER_INFO");
      props.put("basic.auth.user.info", properties.getBasicAuthUserInfo());
    }
  }
}
