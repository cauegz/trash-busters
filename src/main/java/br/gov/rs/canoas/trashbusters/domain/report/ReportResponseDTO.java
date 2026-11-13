package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;

public record ReportResponseDTO (
    String justificativaIa,
    Integer severidade,
    Boolean valido,
    CategoriaLixo categoria,
    InimigoDTO inimigo
) {}
