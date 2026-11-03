package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportRequestDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.services.FileStorageService;
import br.gov.rs.canoas.trashbusters.services.GeminiService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.ai.content.Media;
import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<GeminiResponseDTO> report(@ModelAttribute @Valid ReportRequestDTO data) {
        FileStorageService service = new FileStorageService();
        MultipartFile imagem = data.imagem();

        GeminiResponseDTO response;

        String caminho = service.storeImage(imagem);
        try{
            Media imagemGemini = new Media(
                    MimeTypeUtils.parseMimeType(Objects.requireNonNull(imagem.getContentType())),
                    new ByteArrayResource(imagem.getBytes())
            );

            GeminiService gemini = new GeminiService();
            response = gemini.analisarImagem(imagemGemini);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(response);
    }
}
