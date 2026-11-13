package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class GeminiService {
    private static final Logger LOGGER = Logger.getLogger(GeminiService.class.getName());
    private static final String INSTRUCAO =
    """
    Você é o analisador de descarte irregular de lixo da plataforma TrashBusters.
            Examine a imagem enviada e determine:
            1. "valido": true se houver descarte irregular/acumulado de lixo visível na imagem; false caso contrário.
            2. "categoria": a categoria predominante do material, exclusivamente uma dessas opções: PLASTICO,
                METAL, ORGANICO, PAPEL, VIDRO, REJEITO, ENTULHO, ELETRONICO, HOSPITALAR, NAO_IDENTIFICADO
            3. "severidade": um inteiro de 1 a 5 indicando a gravidade e volume do descarte.
            4. "justificativa": uma frase concisa justificando sua decisão.
    O retorno deve ser EXCLUSIVAMENTE esse:
    {
        "valido": [valor que voce decidir],
        "categoria": [conforme explicado anteriormente],
        "severidade": [numero de 1 a 5],
        "justificativa": [sua justificativa]
    }
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
                        .temperature(0.0))
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

    public static Media getMedia(MultipartFile imagem){

        byte[] conteudoImagem;
        try {
            conteudoImagem = imagem.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler a imagem enviada", e);
        }

        String contentTypeStr = imagem.getContentType();
        assert contentTypeStr != null;

        MimeType mimeType = MimeType.valueOf(contentTypeStr);

        ByteArrayResource resource = new ByteArrayResource(conteudoImagem) {
            @Override
            public String getFilename() {
                return imagem.getOriginalFilename();
            }
        };

        return new Media(mimeType, resource);
    }
}
