package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.logging.Logger;

@Service
public class GeminiService {
    private static final Logger LOGGER = Logger.getLogger(GeminiService.class.getName());
    private static final String INSTRUCAO = """
            Analise a imagem enviada e determine se ela mostra lixo descartado de forma
            irregular. Responda somente com os campos do schema solicitado. Defina isLixo
            como true somente se houver lixo visível. Quando isLixo for true, dano deve ser
            um inteiro de 1 a 10 proporcional à gravidade; caso contrário, dano deve ser 0.
            """;

    private final ChatClient chatClient;
    private final String primaryModel;
    private final String fallbackModel;

    public GeminiService(
            ChatClient.Builder chatClientBuilder,
            @Value("${trashbusters.ai.primary-model}") String primaryModel,
            @Value("${trashbusters.ai.fallback-model:}") String fallbackModel
    ) {
        this.chatClient = chatClientBuilder.build();
        this.primaryModel = primaryModel;
        this.fallbackModel = fallbackModel;
    }

    public GeminiResponseDTO analisarImagem(Media imagem) {
        try {
            return analisarComModelo(imagem, this.primaryModel);
        } catch (RuntimeException primaryException) {
            LOGGER.log(java.util.logging.Level.WARNING, "Modelo Gemini primário falhou: " + primaryException.getMessage(), primaryException);
            if (!StringUtils.hasText(this.fallbackModel) || this.primaryModel.equals(this.fallbackModel)) {
                throw new GeminiIndisponivelException(primaryException);
            }

            LOGGER.warning(() -> "Modelo Gemini primário indisponível; tentando o modelo de fallback.");
            try {
                return analisarComModelo(imagem, this.fallbackModel);
            } catch (RuntimeException fallbackException) {
                fallbackException.addSuppressed(primaryException);
                LOGGER.log(java.util.logging.Level.WARNING, "Modelo Gemini de fallback também falhou: " + fallbackException.getMessage(), fallbackException);
                throw new GeminiIndisponivelException(fallbackException);
            }
        }
    }

    private GeminiResponseDTO analisarComModelo(Media imagem, String modelo) {
        return this.chatClient.prompt()
                .options(GoogleGenAiChatOptions.builder()
                        .model(modelo)
                        .temperature(0.0)
                        .maxOutputTokens(1024)
                        .thinkingLevel(GoogleGenAiThinkingLevel.LOW))
                .system(INSTRUCAO)
                .user(user -> user.text("Classifique esta imagem.").media(imagem))
                .call()
                .entity(GeminiResponseDTO.class);
    }

    public static class GeminiIndisponivelException extends RuntimeException {
        public GeminiIndisponivelException(Throwable cause) {
            super("Não foi possível analisar a imagem com os modelos Gemini configurados", cause);
        }
    }
}
