package io.spectolabs.hoverflymcp;

import io.specto.hoverfly.junit.api.HoverflyClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HoverflyMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(HoverflyMcpApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider hoverflyTools(HoverflyService hoverflyService) {
        return MethodToolCallbackProvider.builder().toolObjects(hoverflyService).build();
    }

    @Bean
    public HoverflyClient hoverflyClient() {
       return HoverflyClient.custom()
                .scheme("http")
                .host("localhost")
                .port(8888)
                .build();
    }
}