package br.com.leroy.merlin.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaConsumerProperties extends CommonKafkaProperties {
  private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
  private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
  private String groupId;
  private String autoOffsetReset = "latest";
  private Boolean enableAutoCommit = true;
  private Integer maxPollRecords = 500;
  private Integer concurrency = 1;
}
