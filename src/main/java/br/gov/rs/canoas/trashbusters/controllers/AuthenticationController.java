package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.user.*;
import br.gov.rs.canoas.trashbusters.infra.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository repository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO data) {
        if (this.repository.existsByLogin(data.login())) {
            return ResponseEntity.badRequest().body("Login já cadastrado.");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        Long papelId = (data.papelId() != null) ? data.papelId() : 1L; // ID padrão para o papel na tabela

        User newUser = new User(data.login(), encryptedPassword, data.nome(), papelId);
        this.repository.save(newUser);

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        User  user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(user);
    }
}