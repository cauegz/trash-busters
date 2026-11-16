package br.gov.rs.canoas.trashbusters.infra.exception;

public class InimigoNotFoundException extends RuntimeException {
    public InimigoNotFoundException(Long id) {
        super("Inimigo não encontrado para o ID: " + id);
    }
}
