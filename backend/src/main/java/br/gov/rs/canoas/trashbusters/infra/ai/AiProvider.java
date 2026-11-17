package br.gov.rs.canoas.trashbusters.infra.ai;

import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import org.springframework.ai.content.Media;

public interface AiProvider {

    String name();

    AiAnalysisResult analyze(Media media);
}
