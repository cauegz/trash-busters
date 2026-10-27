package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.report.ReportRequestDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.services.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    @PostMapping
    public ResponseEntity<ReportResponseDTO> report(@ModelAttribute @Valid ReportRequestDTO data){
        FileStorageService service = new FileStorageService();
        service.storeImage(data.imagem());
        return ResponseEntity.ok(new ReportResponseDTO("aaaaaa"));
    }
}
