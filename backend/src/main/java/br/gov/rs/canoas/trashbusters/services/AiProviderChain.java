package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import br.gov.rs.canoas.trashbusters.infra.ai.AiProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.ai.content.Media;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class AiProviderChain {

    private static final Logger LOGGER = Logger.getLogger(AiProviderChain.class.getName());
    private final List<AiProvider> providers;

    public AiProviderChain(List<AiProvider> providers) {
        if (CollectionUtils.isEmpty(providers)) {
            throw new IllegalStateException("Nenhum provedor de IA foi configurado");
        }
        this.providers = List.copyOf(providers);
    }

    public AiAnalysisResult analyze(Media media) {
        RuntimeException lastFailure = null;

        for (AiProvider provider : providers) {
            try {
                return provider.analyze(media);
            } catch (RuntimeException exception) {
                lastFailure = exception;
                LOGGER.log(Level.WARNING,
                        "Provedor de IA '" + provider.name() + "' falhou; tentando o próximo provedor",
                        exception);
            }
        }

        throw new AiAnalysisService.AiUnavailableException(lastFailure);
    }
}
