package br.gov.rs.canoas.trashbusters.infra.exception;

import java.time.LocalDateTime;

public record ApiErrorDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem
) {
    public ApiErrorDTO(int status, String erro, String mensagem) {
        this(LocalDateTime.now(), status, erro, mensagem);
    }
}