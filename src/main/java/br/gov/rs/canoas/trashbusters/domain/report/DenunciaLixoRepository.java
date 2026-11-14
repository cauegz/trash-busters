package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DenunciaLixoRepository extends JpaRepository<DenunciaLixo, Long> {
    List<DenunciaLixo> findByUsuario(User usuario);
}