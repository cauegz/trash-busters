package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.services.BairroService;
import br.gov.rs.canoas.trashbusters.domain.ai.AiAnalysisResult;
import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportRequestDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import br.gov.rs.canoas.trashbusters.services.*;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.ai.content.Media;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final InimigoService inimigoService;
    private final ReportService reportService;
    private final FileStorageService fileStorageService;
    private final AiAnalysisService aiAnalysisService;

    //4326 é o código padrão pra usar o gps
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final BairroService bairroService;

    public ReportController(InimigoService inimigoService, ReportService reportService, FileStorageService fileStorageService, AiAnalysisService aiAnalysisService, BairroService bairroService) {
        this.inimigoService = inimigoService;
        this.reportService = reportService;
        this.fileStorageService = fileStorageService;
        this.aiAnalysisService = aiAnalysisService;
        this.bairroService = bairroService;
    }

    @PostMapping
    public ResponseEntity<ReportResponseDTO> report(
            @ModelAttribute @Valid ReportRequestDTO data,
            @AuthenticationPrincipal User usuarioLogado
    ) {
        Point coordernadas = geometryFactory.createPoint(new Coordinate(data.longitude(), data.latitude()));

        MultipartFile imagem = data.imagem();

        Media media = AiAnalysisService.getMedia(imagem);

        AiAnalysisResult analysis = this.aiAnalysisService.analisarImagem(media);

        StatusDenuncia status = StatusDenuncia.VALIDATED;

        if (!analysis.valido()) {
            status = StatusDenuncia.REJECTED_AI;
            this.reportService.salvarDenuncia(
                    LocalDateTime.now(), data.endereco(), usuarioLogado,
                    status, coordernadas,
                    analysis.categoria(), analysis.severidade(), analysis.justificativa(),
                    null, null
            );
            return ResponseEntity.badRequest().body(
                    new ReportResponseDTO(
                            analysis.justificativa(),
                            analysis.severidade(),
                            false,
                            analysis.categoria(),
                            null
                    )
            );
        }

        //se chegou aqui e porque e valido
        String caminho = fileStorageService.storeImage(imagem);

        Bairro bairro = bairroService.findByPonto(coordernadas)
                .orElseThrow(() -> new IllegalArgumentException("coordenada fora de canoas"));

        Inimigo inimigo = inimigoService.findByBairroId(bairro.getId())
                .orElseThrow(() -> new IllegalArgumentException("falha ao pegar inimigo"));

        this.reportService.salvarDenuncia(
                LocalDateTime.now(),
                data.endereco(),
                usuarioLogado,
                status,
                coordernadas,
                analysis.categoria(),
                analysis.severidade(),
                analysis.justificativa(),
                caminho,
                inimigo
        );

        return ResponseEntity.ok(new ReportResponseDTO(
                analysis.justificativa(),
                analysis.severidade(),
                true,
                analysis.categoria(),
                InimigoDTO.fromEntity(inimigo)
        ));
    }
}