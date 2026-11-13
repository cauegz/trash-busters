package br.gov.rs.canoas.trashbusters.domain.report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoriaLixo {
    PLASTICO(1.2),
    METAL(1.3),
    ORGANICO(1.0),
    PAPEL(1.1),
    VIDRO(1.4),
    REJEITO(1.0),
    ENTULHO(1.8),
    ELETRONICO(2.0),
    HOSPITALAR(2.5),
    NAO_IDENTIFICADO(1.0);

    private final double multiplicadorDano;
}