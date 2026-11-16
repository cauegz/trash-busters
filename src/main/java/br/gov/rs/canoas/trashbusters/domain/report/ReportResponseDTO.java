package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;

public record ReportResponseDTO (
    Integer id,
    String justificativaIa,
    Integer severidade,
    Boolean valido,
    CategoriaLixo categoria,
    Integer dano,
    Double latitude, // y
    Double longitude, // x
    StatusDenuncia statusDenuncia,
    InimigoDTO inimigo
) {
    public static ReportResponseDTO fromEntity(DenunciaLixo denunciaLixo){
        Double latitude = denunciaLixo.getCoordenadas() == null
                ? null : denunciaLixo.getCoordenadas().getY();
        Double longitude = denunciaLixo.getCoordenadas() == null
                ? null : denunciaLixo.getCoordenadas().getX();

        return new ReportResponseDTO(
                denunciaLixo.getId(),
                denunciaLixo.getJustificativaIa(),
                denunciaLixo.getSeveridade(),
                denunciaLixo.getStatus() == StatusDenuncia.VALIDATED,
                denunciaLixo.getCategoria(),
                denunciaLixo.getDano(),
                latitude,
                longitude,
                denunciaLixo.getStatus(),
                denunciaLixo.getInimigo() == null
                        ? null : InimigoDTO.fromEntity(denunciaLixo.getInimigo())
        );
    }
}
