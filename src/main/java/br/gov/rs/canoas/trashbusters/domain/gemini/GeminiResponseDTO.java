package br.gov.rs.canoas.trashbusters.domain.gemini;

import br.gov.rs.canoas.trashbusters.domain.report.CategoriaLixo;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiResponseDTO(
        @JsonProperty("valido")
        boolean valido,

        @JsonProperty("categoria")
        CategoriaLixo categoria,

        @JsonProperty("severidade")
        int severidade, // Nível de gravidade/volume de 1 a 5

        @JsonProperty("justificativa")
        String justificativa // Explicação curta do motivo da aprovação ou rejeição
) {
}