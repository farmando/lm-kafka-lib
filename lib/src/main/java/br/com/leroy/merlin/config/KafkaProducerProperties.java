package br.com.leroy.merlin.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class KafkaProducerProperties extends CommonKafkaProperties {
  private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
  private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
  private String compressionType = "none";
  private Integer retries = 3;
  private Integer batchSize = 16384;
  private String clientId;
  private Integer deliveryTimeoutMs = 30000;
  private Integer lingerMs = 0;
  private Integer requestTimeoutMs = 30000;
  private String acks = "all";
  private Boolean enableIdempotence = true;
  private Integer maxInFlightRequestsPerConnection = 5;
  private Boolean autoRegisterSchemas = true;
  private String schemaStrategy = "TOPIC_NAME";
}
