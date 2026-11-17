package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

import java.nio.file.Path;

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

    public Media getMedia(Path imagePath) {
        Resource resource = new FileSystemResource(imagePath);
        return new Media(MimeType.valueOf("image/jpeg"), resource);
    }
}
