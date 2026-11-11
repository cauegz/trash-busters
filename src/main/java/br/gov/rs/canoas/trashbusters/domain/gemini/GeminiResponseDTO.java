package br.gov.rs.canoas.trashbusters.domain.gemini;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiResponseDTO(
        @JsonProperty("isLixo") boolean isLixo,
        @JsonProperty("dano") int dano
) {
}