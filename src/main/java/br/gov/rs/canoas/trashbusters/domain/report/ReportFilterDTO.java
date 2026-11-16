package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;

import java.time.LocalDateTime;

public record ReportFilterDTO(
        StatusDenuncia statusDenuncia,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        Double latitude,
        Double longitude,
        Double raio,
        Integer severidadeMinima,
        CategoriaLixo categoriaLixo,
        Integer bairroId,
        Integer inimigoId
) {}
