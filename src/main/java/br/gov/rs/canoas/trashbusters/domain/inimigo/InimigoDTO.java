package br.gov.rs.canoas.trashbusters.domain.inimigo;

import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;

public record InimigoDTO(
    Integer id,
    String nome,
    StatusInimigo statusInimigo,
    Integer hpAtual,
    Integer hpMaximo,
    String bairro
) {
    public static InimigoDTO fromEntity(Inimigo inimigo){
        if (inimigo == null) return null;

        return new InimigoDTO(
                inimigo.getId(),
                inimigo.getNome(),
                inimigo.getStatus(),
                inimigo.getHpAtual(),
                inimigo.getHpMax(),
                inimigo.getBairro() == null ? null : inimigo.getBairro().getNome()
        );
    }
}
