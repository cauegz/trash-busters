package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.CategoriaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixoRepository;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;
import br.gov.rs.canoas.trashbusters.domain.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ReportServiceTest {
    @Test
    void devePersistirDenunciaEAplicarDanoAoInimigo() {
        DenunciaLixoRepository repository = mock(DenunciaLixoRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReportService service = new ReportService(repository, userRepository);
        Inimigo inimigo = new Inimigo();
        inimigo.setHpAtual(200);
        inimigo.setStatus(StatusInimigo.ATIVO);

        service.salvarDenuncia(
                LocalDateTime.now(),
                "Rua Exemplo, 100",
                null,
                StatusDenuncia.VALIDATED,
                new GeometryFactory().createPoint(),
                CategoriaLixo.ENTULHO,
                3,
                "Descarte irregular identificado",
                "imagem.jpg",
                inimigo
        );

        assertEquals(80, inimigo.getHpAtual());
        assertEquals(StatusInimigo.ATIVO, inimigo.getStatus());

        ArgumentCaptor<DenunciaLixo> captor = ArgumentCaptor.forClass(DenunciaLixo.class);
        verify(repository).save(captor.capture());
        assertEquals(StatusDenuncia.VALIDATED, captor.getValue().getStatus());
        assertEquals(CategoriaLixo.ENTULHO, captor.getValue().getCategoria());
        assertEquals(3, captor.getValue().getSeveridade());
        assertEquals("imagem.jpg", captor.getValue().getCaminhoImagem());
    }

    @Test
    void devePersistirDenunciaRejeitadaSemAplicarDano() {
        DenunciaLixoRepository repository = mock(DenunciaLixoRepository.class);
        UserRepository userRepository = mock(UserRepository.class);

        ReportService service = new ReportService(repository, userRepository);

        service.salvarDenuncia(
                LocalDateTime.now(),
                "Rua Exemplo, 100",
                null,
                StatusDenuncia.REJECTED_AI,
                new GeometryFactory().createPoint(),
                CategoriaLixo.NAO_IDENTIFICADO,
                1,
                "A imagem não contém lixo",
                null,
                null
        );

        ArgumentCaptor<DenunciaLixo> captor = ArgumentCaptor.forClass(DenunciaLixo.class);
        verify(repository).save(captor.capture());
        assertEquals(StatusDenuncia.REJECTED_AI, captor.getValue().getStatus());
        assertNull(captor.getValue().getInimigo());
        assertNull(captor.getValue().getCaminhoImagem());
    }
}
