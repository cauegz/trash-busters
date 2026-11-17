package br.gov.rs.canoas.trashbusters.domain.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StatusInimigo {
    ATIVO(1L),
    DERROTADO(2L);

    private final Long id;

    public static StatusInimigo fromId(Long id) {
        for (StatusInimigo s : values()) {
            if (s.id.equals(id)) return s;
        }
        throw new IllegalArgumentException("Status ID inválido: " + id);
    }
}