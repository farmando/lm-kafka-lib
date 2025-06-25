package br.com.leroy.merlin.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KafkaConfigurationHelperTest {

  @Mock
  private CommonKafkaProperties properties;

  @Test
  void shouldNotConfigureSecurityWhenProtocolIsNull() {
    // given
    when(properties.getSecurityProtocol()).thenReturn(null);
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSecurityProperties(props, properties);

    // then
    assertTrue(props.isEmpty());
  }

  @Test
  void shouldConfigureBasicSecurityWithPlaintext() {
    // given
    when(properties.getSecurityProtocol()).thenReturn("PLAINTEXT");
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSecurityProperties(props, properties);

    // then
    assertEquals(1, props.size());
    assertEquals("PLAINTEXT", props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
  }

  @Test
  void shouldConfigureSecurityWithSaslAndSsl() {
    // given
    when(properties.getSecurityProtocol()).thenReturn("SASL_SSL");
    when(properties.getSaslMechanism()).thenReturn("PLAIN");
    when(properties.getSaslJaasConfig()).thenReturn(
        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";");
    when(properties.getSslEndpointIdentificationAlgorithm()).thenReturn("https");
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSecurityProperties(props, properties);

    // then
    assertEquals(4, props.size());
    assertEquals("SASL_SSL", props.get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
    assertEquals("PLAIN", props.get(SaslConfigs.SASL_MECHANISM));
    assertEquals(
        "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"pass\";",
        props.get(SaslConfigs.SASL_JAAS_CONFIG));
    assertEquals("https", props.get(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG));
  }

  @Test
  void shouldNotConfigureSchemaRegistryWhenUrlIsNull() {
    // given
    when(properties.getSchemaRegistryUrl()).thenReturn(null);
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, properties);

    // then
    assertTrue(props.isEmpty());
  }

  @Test
  void shouldConfigureSchemaRegistryWithUrl() {
    // given
    when(properties.getSchemaRegistryUrl()).thenReturn("http://schema-registry:8081");
    when(properties.getBasicAuthUserInfo()).thenReturn(null);
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, properties);

    // then
    assertEquals(1, props.size());
    assertEquals("http://schema-registry:8081", props.get("schema.registry.url"));
  }

  @Test
  void shouldConfigureSchemaRegistryWithAuth() {
    // given
    when(properties.getSchemaRegistryUrl()).thenReturn("http://schema-registry:8081");
    when(properties.getBasicAuthUserInfo()).thenReturn("user:password");
    when(properties.getBasicAuthCredentialsSource()).thenReturn("USER_INFO");
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, properties);

    // then
    assertEquals(3, props.size());
    assertEquals("http://schema-registry:8081", props.get("schema.registry.url"));
    assertEquals("USER_INFO", props.get("basic.auth.credentials.source"));
    assertEquals("user:password", props.get("basic.auth.user.info"));
  }

  @Test
  void shouldUseDefaultCredentialsSourceWhenNotSpecified() {
    // given
    when(properties.getSchemaRegistryUrl()).thenReturn("http://schema-registry:8081");
    when(properties.getBasicAuthUserInfo()).thenReturn("user:password");
    when(properties.getBasicAuthCredentialsSource()).thenReturn(null);
    Map<String, Object> props = new HashMap<>();

    // when
    KafkaConfigurationHelper.configureSchemaRegistryProperties(props, properties);

    // then
    assertEquals(3, props.size());
    assertEquals("http://schema-registry:8081", props.get("schema.registry.url"));
    assertEquals("USER_INFO", props.get("basic.auth.credentials.source"));
    assertEquals("user:password", props.get("basic.auth.user.info"));
  }
}