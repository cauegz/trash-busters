package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.bairro.BairroDTO;
import br.gov.rs.canoas.trashbusters.services.BairroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bairros")
public class BairroController {
    private final BairroService bairroService;

    public BairroController(BairroService bairroService) {
        this.bairroService = bairroService;
    }

    @GetMapping
    public ResponseEntity<List<BairroDTO>> getBairros() {
        return ResponseEntity.ok(bairroService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BairroDTO> getBairroById(@PathVariable Long id) {
        return ResponseEntity.ok(bairroService.findById(id));
    }
}
