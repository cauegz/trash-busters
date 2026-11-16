package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.*;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import br.gov.rs.canoas.trashbusters.domain.user.UserRepository;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<ReportResponseDTO> filtrar(ReportFilterDTO filtro, Pageable pageable) {
        validarFiltroEspacial(filtro);

        Specification<DenunciaLixo> spec = Specification.allOf(
                DenunciaSpecs.comCategoria(filtro.categoriaLixo()),
                DenunciaSpecs.comStatus(filtro.statusDenuncia()),
                DenunciaSpecs.comSeveridadeMinima(filtro.severidadeMinima()),
                DenunciaSpecs.doBairro(filtro.bairroId()),
                DenunciaSpecs.doInimigo(filtro.inimigoId()),
                DenunciaSpecs.proximoDe(filtro.latitude(), filtro.longitude(), filtro.raio()),
                DenunciaSpecs.criadoEntre(filtro.dataInicio(), filtro.dataFim())
        );

        return denunciaRepository.findAll(spec, pageable)
                .map(ReportResponseDTO::fromEntity);
    }

    private void validarFiltroEspacial(ReportFilterDTO filtro) {
        boolean possuiLatitude = filtro.latitude() != null;
        boolean possuiLongitude = filtro.longitude() != null;
        boolean possuiRaio = filtro.raio() != null;

        if (possuiLatitude || possuiLongitude || possuiRaio) {
            if (!possuiLatitude || !possuiLongitude || !possuiRaio) {
                throw new IllegalArgumentException(
                        "latitude, longitude e raio devem ser informados juntos"
                );
            }
            if (filtro.latitude() < -90 || filtro.latitude() > 90
                    || filtro.longitude() < -180 || filtro.longitude() > 180
                    || filtro.raio() <= 0) {
                throw new IllegalArgumentException("filtro espacial inválido");
            }
        }
    }

    private Integer calcularDano(CategoriaLixo categoria, Integer severidade){
        return (int) ((DANO_BASE * categoria.getMultiplicadorDano()) + severidade * 10);
    }
}
