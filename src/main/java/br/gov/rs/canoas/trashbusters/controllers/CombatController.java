package br.gov.rs.canoas.trashbusters.controllers;

import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;
import br.gov.rs.canoas.trashbusters.infra.exception.InimigoNotFoundException;
import br.gov.rs.canoas.trashbusters.services.InimigoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/combat")
public class CombatController {
    private final InimigoService inimigoService;

    public CombatController(InimigoService inimigoService) {
        this.inimigoService = inimigoService;
    }

    @GetMapping("/active")
    public ResponseEntity<List<InimigoDTO>> listaInmigosAtivos() {
        return ResponseEntity.ok(inimigoService.listarTodosAtivos());
    }

    @GetMapping("/defeated")
    public ResponseEntity<List<InimigoDTO>> listaInmigosDerrotados() {
        return ResponseEntity.ok(inimigoService.listarTodosDerrotados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InimigoDTO> getEnemieById(@PathVariable Long id) {
        return ResponseEntity.ok(
                inimigoService.getInimigoById(id)
                        .map(InimigoDTO::fromEntity)
                        .orElseThrow(() -> new InimigoNotFoundException(id))
        );
    }
}
