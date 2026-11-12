package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixoRepository;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportService {
    private final DenunciaLixoRepository denunciaRepository;

    public ReportService(DenunciaLixoRepository denunciaRepository) {
        this.denunciaRepository = denunciaRepository;
    }

    @Transactional
    public void salvarDenuncia(LocalDateTime criadoEm, String localizacao, User usuario, Inimigo inimigo, StatusDenuncia status, Double latitude, Double longitude){


        DenunciaLixo denuncia = new DenunciaLixo();
        denuncia.setCriadoEm(criadoEm);
        denuncia.setEndereco(localizacao);
        denuncia.setUsuario(usuario);
        denuncia.setInimigo(inimigo);
        denuncia.setStatus(status);

        this.denunciaRepository.save(denuncia);
    }
}
