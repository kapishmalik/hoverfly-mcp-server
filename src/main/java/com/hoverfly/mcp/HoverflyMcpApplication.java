package com.hoverfly.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.tool.HoverflyService;
import io.specto.hoverfly.junit.api.HoverflyClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HoverflyMcpApplication {

  public static void main(String[] args) {
    SpringApplication.run(HoverflyMcpApplication.class, args);
  }

  @Bean
  public HoverflyClient hoverflyClient(
      @Value("${hoverfly.admin.scheme:http}") String scheme,
      @Value("${hoverfly.admin.host:localhost}") String host,
      @Value("${hoverfly.admin.port:8888}") int port) {
    return HoverflyClient.custom().scheme(scheme).host(host).port(port).build();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public ToolCallbackProvider toolCallbackProvider(HoverflyService hoverflyService) {
    return MethodToolCallbackProvider.builder().toolObjects(hoverflyService).build();
  }
}
