package br.gov.rs.canoas.trashbusters.domain.inimigo;

public record InimigoDTO(
    Integer id,
    String nome,
    Integer hpAtual,
    Integer hpMaximo,
    String bairro
) {
    public static InimigoDTO fromEntity(Inimigo inimigo){
        return new InimigoDTO(
                inimigo.getId(),
                inimigo.getNome(),
                inimigo.getHpAtual(),
                inimigo.getHpMax(),
                inimigo.getBairro().getNome()
        );
    }
}
