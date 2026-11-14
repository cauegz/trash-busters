package br.gov.rs.canoas.trashbusters.domain.inimigo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InimigoRepository extends JpaRepository<Inimigo, Long> {
    Optional<Inimigo> findByBairroId(Long bairroId);
}