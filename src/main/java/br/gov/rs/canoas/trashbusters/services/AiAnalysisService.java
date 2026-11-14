package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

@Service
public class AiAnalysisService {

    private final AiProviderChain providerChain;

    public AiAnalysisService(AiProviderChain providerChain) {
        this.providerChain = providerChain;
    }

    public AiAnalysisResult analisarImagem(Media imagem) {
        return providerChain.analyze(imagem);
    }

    public static class AiUnavailableException extends RuntimeException {
        public AiUnavailableException(Throwable cause) {
            super("Não foi possível analisar a imagem com os provedores de IA configurados", cause);
        }
    }

    public static Media getMedia(MultipartFile imagem) {
        byte[] conteudoImagem;
        try {
            conteudoImagem = imagem.getBytes();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler a imagem enviada", exception);
        }

        String contentType = imagem.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("A imagem enviada não possui um tipo MIME válido");
        }

        ByteArrayResource resource = new ByteArrayResource(conteudoImagem) {
            @Override
            public String getFilename() {
                return imagem.getOriginalFilename();
            }
        };

        return new Media(MimeType.valueOf(contentType), resource);
    }
}
