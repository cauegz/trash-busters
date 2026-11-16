package br.gov.rs.canoas.trashbusters.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name = "usuario")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    private String senha;

    private Integer xp = 0;

    private String nome;

    @Column(name = "papel_id")
    private Long papelId;

    public User(String login, String senha, String nome, Long papelId) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.papelId = papelId;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        Role role = Role.fromId(this.papelId);

        return switch (role) {
            case ADMIN -> List.of(
                    new SimpleGrantedAuthority(Role.ADMIN.getAuthority()),
                    new SimpleGrantedAuthority(Role.INSPECTOR.getAuthority()),
                    new SimpleGrantedAuthority(Role.PLAYER.getAuthority())
            );
            case INSPECTOR -> List.of(
                    new SimpleGrantedAuthority(Role.INSPECTOR.getAuthority()),
                    new SimpleGrantedAuthority(Role.PLAYER.getAuthority())
            );
            case PLAYER -> List.of(
                    new SimpleGrantedAuthority(Role.PLAYER.getAuthority())
            );
        };
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public @NonNull String getUsername() {
        return login;
    }

    public void receberXp(Integer xp){
        this.xp = this.getXp() + xp;
    }
}