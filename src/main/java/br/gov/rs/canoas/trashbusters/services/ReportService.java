package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.domain.bairro.BairroRepository;
import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoRepository;
import br.gov.rs.canoas.trashbusters.domain.report.CategoriaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixoRepository;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReportService {
    private final DenunciaLixoRepository denunciaRepository;

    public ReportService(DenunciaLixoRepository denunciaRepository) {
        this.denunciaRepository = denunciaRepository;
    }

    @Transactional
    public void salvarDenuncia(
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

        this.denunciaRepository.save(denuncia);
    }

    
}
