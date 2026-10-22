package br.gov.rs.canoas.trashbusters;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Teste {
    @GetMapping("/teste")
    public void teste(){
        return;
    }
}
