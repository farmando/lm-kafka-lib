package br.com.leroy.merlin.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class KafkaConfigCustomizerTest {

  @Test
  void shouldCustomizeConfigProperties() {
    // Arrange
    Map<String, Object> configProperties = new HashMap<>();
    configProperties.put("existing.property", "value");

    KafkaConfigCustomizer customizer = properties -> {
      properties.put("custom.property", "custom-value");
      properties.put("existing.property", "modified-value");
    };

    // Act
    customizer.customize(configProperties);

    // Assert
    assertEquals("modified-value", configProperties.get("existing.property"));
    assertEquals("custom-value", configProperties.get("custom.property"));
    assertEquals(2, configProperties.size());
  }
}