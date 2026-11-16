package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.user.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DenunciaLixoRepository extends JpaRepository<DenunciaLixo, Integer>, JpaSpecificationExecutor<DenunciaLixo> {

    List<DenunciaLixo> findByUsuario(User usuario);

    @Override
    @EntityGraph(attributePaths = {"inimigo", "inimigo.bairro"})
    @NonNull
    Page<DenunciaLixo> findAll(@NonNull Specification<DenunciaLixo> spec, @NonNull Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"inimigo", "inimigo.bairro"})
    @NonNull
    List<DenunciaLixo> findAll(@NonNull Specification<DenunciaLixo> spec, @NonNull Sort sort);
}