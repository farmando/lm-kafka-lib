package br.com.leroy.merlin.config;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum KafkaSchemaStrategyEnum {
  CUSTOM("Custom", "Manually defined",
      "Unique schemas, multi-environment setups",
      null),
  TOPIC_NAME("Topic Name",
      "Based on topic name",
      "Topic-specific schemas",
      "io.confluent.kafka.serializers.subject.TopicNameStrategy"),
  RECORD_NAME("Record Name",
      "Based on record/class name",
      "Reusable schemas across topics",
      "io.confluent.kafka.serializers.subject.RecordNameStrategy"),
  TOPIC_RECORD_NAME("Topic + Record Name",
      "Based on topic and record",
      "Isolated schema versions per (topic, record) pair",
      "io.confluent.kafka.serializers.subject.TopicRecordNameStrategy");

  private final String strategyName;
  private final String schemaDerivation;
  private final String bestFor;
  private final String configValue;

  KafkaSchemaStrategyEnum(String strategyName, String schemaDerivation, String bestFor, String configValue) {
    this.strategyName = strategyName;
    this.schemaDerivation = schemaDerivation;
    this.bestFor = bestFor;
    this.configValue = configValue;
  }
}
