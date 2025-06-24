package br.com.leroy.merlin.config;

import java.util.Map;

public interface KafkaConfigCustomizer {
  void customize(Map<String, Object> configProperties);
}
