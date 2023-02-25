package com.xstudy.media.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

  @Value("${minio.endpoint}")
  private String endpoint;

  @Value("${minio.accesskey}")
  private String accessKey;

  @Value("${minio.secretkey}")
  private String secretKey;

  @Bean
  public MinioClient minioClient() {
    return MinioClient.builder().credentials(accessKey, secretKey).endpoint(endpoint).build();
  }
}
