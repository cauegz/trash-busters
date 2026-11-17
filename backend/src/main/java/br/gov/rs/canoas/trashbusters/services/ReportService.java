package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.CategoriaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixoRepository;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import br.gov.rs.canoas.trashbusters.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {
    private static final Integer DANO_BASE = 50;
    private final DenunciaLixoRepository denunciaRepository;
    private final UserRepository usuarioRepository;

    public ReportService(DenunciaLixoRepository denunciaRepository, UserRepository usuarioRepository) {
        this.denunciaRepository = denunciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Integer salvarDenuncia(
            LocalDateTime criadoEm,
            String endereco,
            User usuario,
            StatusDenuncia status,
            Point coordernadas,
            CategoriaLixo categoriaLixo,
            Integer severidade,
            String justificativaIa,
            String caminhoImagem,
            Inimigo inimigo
    ){
        DenunciaLixo denuncia = new DenunciaLixo();

        Integer dano = this.calcularDano(categoriaLixo, severidade);

        if(inimigo != null && inimigo.getStatus() != StatusInimigo.DERROTADO){
            inimigo.receberDano(dano, StatusInimigo.DERROTADO);

            usuario.receberXp(dano);
            usuarioRepository.save(usuario);
        }

        denuncia.setCriadoEm(criadoEm);
        denuncia.setEndereco(endereco);
        denuncia.setUsuario(usuario);
        denuncia.setInimigo(inimigo);
        denuncia.setStatus(status);
        denuncia.setCoordenadas(coordernadas);
        denuncia.setCategoria(categoriaLixo);
        denuncia.setSeveridade(severidade);
        denuncia.setJustificativaIa(justificativaIa);
        denuncia.setCaminhoImagem(caminhoImagem);
        denuncia.setDano(dano);

        this.denunciaRepository.save(denuncia);

        return dano;
    }

    public List<ReportResponseDTO> getAllReportsByUser(User usuario){
        return denunciaRepository.findByUsuario(usuario)
                .stream()
                .map(ReportResponseDTO::fromEntity)
                .toList();
    }

    private Integer calcularDano(CategoriaLixo categoria, Integer severidade){
        return (int) ((DANO_BASE * categoria.getMultiplicadorDano()) + severidade * 10);
    }
}
