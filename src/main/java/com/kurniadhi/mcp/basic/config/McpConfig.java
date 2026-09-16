package com.kurniadhi.mcp.basic.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kurniadhi.mcp.basic.api.DocumentController;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ToolCallbackProvider documentToolCallbackProvider(DocumentController controller) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(controller)
                .build();
    }
}
