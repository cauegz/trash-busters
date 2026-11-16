package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.report.ReportFilterDTO;
import br.gov.rs.canoas.trashbusters.domain.report.ReportResponseDTO;
import br.gov.rs.canoas.trashbusters.services.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inspector")
public class InspectorController {
    private final ReportService reportService;

    public InspectorController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<ReportResponseDTO>> listar(
            ReportFilterDTO filtro,
            @PageableDefault(size = 20, sort = "criadoEm", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.filtrar(filtro, pageable));
    }
}
