package br.gov.rs.canoas.trashbusters.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN(1L, "ROLE_ADMIN"),
    PLAYER(2L, "ROLE_PLAYER"),
    INSPECTOR(3L, "ROLE_INSPECTOR");

    private final Long id;
    private final String authority;

    public static Role fromId(Long id) {
        return Arrays.stream(values())
                .filter(role -> role.id.equals(id))
                .findFirst()
                .orElse(PLAYER);
    }
}
