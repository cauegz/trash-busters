package br.gov.rs.canoas.trashbusters.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    PLAYER("ROLE_PLAYER"),
    INSPECTOR("ROLE_INSPECTOR");

    private final String role;
}