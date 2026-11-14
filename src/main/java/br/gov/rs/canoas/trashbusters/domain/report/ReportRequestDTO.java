package br.gov.rs.canoas.trashbusters.domain.report;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import org.springframework.web.multipart.MultipartFile;

public record ReportRequestDTO(
        @NotBlank(message = "O endereço é obrigatório")
        String endereco,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "A longitude deve estar entre -180 e 180")
        @DecimalMax(value = "180.0", message = "A longitude deve estar entre -180 e 180")
        Double longitude, //x

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "A latitude deve estar entre -90 e 90")
        @DecimalMax(value = "90.0", message = "A latitude deve estar entre -90 e 90")
        Double latitude, //y

        @NotNull(message = "A imagem é obrigatória")
        MultipartFile imagem
) {
    @AssertTrue(message = "A imagem não pode estar vazia")
    public boolean isImagemValida() {
        return imagem != null && !imagem.isEmpty();
    }
}
