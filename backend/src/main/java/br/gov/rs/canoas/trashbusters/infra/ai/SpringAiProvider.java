package br.gov.rs.canoas.trashbusters.infra.ai;

import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class SpringAiProvider implements AiProvider {

    private static final String INSTRUCTION =
            """
            Você é o analisador de descarte irregular de lixo da plataforma TrashBusters.
            Examine a imagem enviada e determine:
            1. "valido": true se houver descarte irregular/acumulado de lixo visível na imagem; false caso contrário.
            2. "categoria": exclusivamente PLASTICO, METAL, ORGANICO, PAPEL, VIDRO, REJEITO,
               ENTULHO, ELETRONICO, HOSPITALAR ou NAO_IDENTIFICADO.
            3. "severidade": um inteiro de 1 a 5 indicando a gravidade e volume do descarte.
            4. "justificativa": uma frase concisa justificando sua decisão.
            O retorno deve ser exclusivamente um objeto JSON com os campos valido, categoria,
            severidade e justificativa.
            """;

    private final ChatClient chatClient;
    private final String providerName;

    public SpringAiProvider(
            ChatClient.Builder chatClientBuilder,
            @Value("${ai.provider.name:default}") String providerName
    ) {
        this.chatClient = chatClientBuilder.build();
        this.providerName = providerName;
    }

    @Override
    public String name() {
        return providerName;
    }

    @Override
    public AiAnalysisResult analyze(Media media) {
        return chatClient.prompt()
                .system(INSTRUCTION)
                .user(user -> user.text("Classifique esta imagem.").media(media))
                .call()
                .entity(AiAnalysisResult.class);
    }
}
