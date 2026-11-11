package br.gov.rs.canoas.trashbusters.infra.ai;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfig {

    @Bean
    public Client googleGenAiClient(@Value("${spring.ai.google.genai.api-key}") String apiKey) {
        return Client.builder()
                .apiKey(apiKey)
                .httpOptions(HttpOptions.builder()
                        .timeout(45_000)
                        .retryOptions(HttpRetryOptions.builder().attempts(1).build())
                        .build())
                .build();
    }
}