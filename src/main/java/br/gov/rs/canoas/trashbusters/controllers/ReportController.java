package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.ReportRequestDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import br.gov.rs.canoas.trashbusters.services.*;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.ai.content.Media;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final InimigoService inimigoService;
    private final ReportService reportService;
    private final FileStorageService fileStorageService;
    private final GeminiService geminiService;

    public ReportController(InimigoService inimigoService, ReportService reportService, FileStorageService fileStorageService, GeminiService geminiService) {
        this.inimigoService = inimigoService;
        this.reportService = reportService;
        this.fileStorageService = fileStorageService;
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<GeminiResponseDTO> report(
            @ModelAttribute @Valid ReportRequestDTO data,
            @AuthenticationPrincipal User usuarioLogado
    ) {
        MultipartFile imagem = data.imagem();

        GeminiResponseDTO response;

        byte[] conteudoImagem;
        try {
            conteudoImagem = imagem.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível ler a imagem enviada", e);
        }

        String caminho = this.fileStorageService.storeImage(imagem);
        Media imagemGemini = new Media(
                MimeTypeUtils.parseMimeType(Objects.requireNonNull(imagem.getContentType())),
                new ByteArrayResource(conteudoImagem)
        );

        try {
            response = this.geminiService.analisarImagem(imagemGemini);
        } catch (GeminiService.GeminiIndisponivelException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new GeminiResponseDTO(false, 0));
        }

        if(!response.isLixo()){
            return ResponseEntity.badRequest().body(response);
        }

        Inimigo inimigo = this.inimigoService.getInimigoById(data.inimigoId())
                .orElseThrow(() -> new RuntimeException("inimigo não encontrado"));

        this.reportService.salvarDenuncia(LocalDateTime.now(), data.endereco(), usuarioLogado, inimigo);

        return ResponseEntity.ok(response);
    }
}
