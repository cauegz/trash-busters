package br.gov.rs.canoas.trashbusters.infra.exception;

import br.gov.rs.canoas.trashbusters.services.GeminiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeminiService.GeminiIndisponivelException.class)
    public ResponseEntity<ApiErrorDTO> handleGeminiIndisponivel(GeminiService.GeminiIndisponivelException ex) {
        ApiErrorDTO erro = new ApiErrorDTO(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "Serviço de IA indisponível",
                ex.getMessage() != null ? ex.getMessage() : "Não foi possível validar a imagem no momento. Tente novamente em instantes."
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(erro);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ApiErrorDTO> handleRegrasDeNegocio(RuntimeException ex) {
        ApiErrorDTO erro = new ApiErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Requisição inválida",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(erro);
    }
}