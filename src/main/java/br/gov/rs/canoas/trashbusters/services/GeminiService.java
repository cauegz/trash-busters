package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private static final String INSTRUCAO = """
            Analise a imagem enviada e determine se ela mostra lixo descartado de forma
            irregular. Responda somente com os campos do schema solicitado. Defina isLixo
            como true somente se houver lixo visível. Quando isLixo for true, dano deve ser
            um inteiro de 1 a 10 proporcional à gravidade; caso contrário, dano deve ser 0.
            """;

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public GeminiResponseDTO analisarImagem(Media imagem) {
        return this.chatClient.prompt()
                .system(INSTRUCAO)
                .user(user -> user.text("Classifique esta imagem.").media(imagem))
                .call()
                .entity(GeminiResponseDTO.class);
    }
}
