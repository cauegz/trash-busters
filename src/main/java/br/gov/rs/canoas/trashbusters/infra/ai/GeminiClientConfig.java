package br.gov.rs.canoas.trashbusters.infra.ai;

import com.google.genai.Client;
import com.google.genai.types.HttpOptions;
import com.google.genai.types.HttpRetryOptions;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;

@Configuration
public class GeminiClientConfig {

    private static final Logger LOGGER = Logger.getLogger(GeminiClientConfig.class.getName());

    @Bean
    public Client googleGenAiClient(@Value("${spring.ai.google.genai.api-key:}") String apiKeyProperty) {
        String key = apiKeyProperty;
        String origem = "application.properties";

        if (!StringUtils.hasText(key) || key.startsWith("${")) {
            key = System.getenv("GOOGLE_API_KEY");
            origem = "System.getenv(GOOGLE_API_KEY)";
        }
        if (!StringUtils.hasText(key) || key.startsWith("${")) {
            key = System.getenv("GEMINI_API_KEY");
            origem = "System.getenv(GEMINI_API_KEY)";
        }

        if (!StringUtils.hasText(key) || key.startsWith("${")) {
            throw new IllegalStateException("Nenhuma chave de API válida foi encontrada no application.properties ou no ambiente (GOOGLE_API_KEY / GEMINI_API_KEY)!");
        }

        String maskedKey = key.length() > 8
                ? key.substring(0, 4) + "..." + key.substring(key.length() - 4)
                : "INVALID";

        LOGGER.info("=== [TRASHBUSTERS AI] Inicializando GoogleGenAiClient ===");
        LOGGER.info("=== Origem da Chave: " + origem);
        LOGGER.info("=== Chave Selecionada (mascarada): " + maskedKey);

        return Client.builder()
                .apiKey(key)
                .httpOptions(HttpOptions.builder()
                        .timeout(45_000)
                        .retryOptions(HttpRetryOptions.builder().attempts(1).build())
                        .build())
                .build();
    }

    @Bean
    public GoogleGenAiChatModel googleGenAiChatModel(
            Client googleGenAiClient,
            @Value("${trashbusters.ai.primary-model:gemini-3.5-flash}") String defaultModel
    ) {
        LOGGER.info("=== [TRASHBUSTERS AI] Configurando GoogleGenAiChatModel ===");
        LOGGER.info("=== Modelo Primário Alvo: " + defaultModel);

        GoogleGenAiChatOptions options = GoogleGenAiChatOptions.builder()
                .model(defaultModel)
                .temperature(0.0)
                .build();

        ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();
        ObservationRegistry observationRegistry = ObservationRegistry.NOOP;
        RetryTemplate retryTemplate = RetryUtils.DEFAULT_RETRY_TEMPLATE;

        return new GoogleGenAiChatModel(
                googleGenAiClient,
                options,
                toolCallingManager,
                retryTemplate,
                observationRegistry
        );
    }

    @Bean
    public ChatClient.Builder chatClientBuilder(GoogleGenAiChatModel chatModel) {
        return ChatClient.builder(chatModel);
    }
}