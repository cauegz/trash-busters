package br.gov.rs.canoas.trashbusters.domain.bairro;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;

public record BairroDTO(
        Long id,
        String codigoIbge,
        String nome,
        InimigoDTO inimigo
) {
    public static BairroDTO fromEntity(Bairro bairro, Inimigo inimigo){
        return new BairroDTO(
                bairro.getId(),
                bairro.getCodigoIbge(),
                bairro.getNome(),
                InimigoDTO.fromEntity(inimigo)
        );
    }
}
