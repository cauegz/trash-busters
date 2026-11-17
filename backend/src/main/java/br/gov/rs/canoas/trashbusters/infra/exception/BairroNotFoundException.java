package br.gov.rs.canoas.trashbusters.infra.exception;

public class BairroNotFoundException extends RuntimeException {
    public BairroNotFoundException(Long id) {
        super("Bairro não encontrado para o ID: " + id);
    }
}
