package br.com.leroy.merlin.config;

import lombok.Data;

@Data
public class CommonKafkaProperties {
  private String bootstrapServers;
  private String securityProtocol;
  private String saslMechanism;
  private String saslJaasConfig;
  private String schemaRegistryUrl;
  private String schemaRegistryCredential;
  private String sslEndpointIdentificationAlgorithm;
  private String basicAuthUserInfo;
  private String basicAuthCredentialsSource;
}
