package br.gov.rs.canoas.trashbusters.domain.user;

public record RegisterRequestDTO(String login, String password, String nome, Long papelId) {
}