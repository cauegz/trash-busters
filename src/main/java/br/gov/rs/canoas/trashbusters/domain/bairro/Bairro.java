package br.gov.rs.canoas.trashbusters.domain.bairro;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "bairro")
public class Bairro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String nome;
    @OneToMany(mappedBy = "bairro")
    private List<Inimigo> inimigoList = new ArrayList<>();
}