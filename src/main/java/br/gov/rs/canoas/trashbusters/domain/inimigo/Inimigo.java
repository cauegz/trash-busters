package br.gov.rs.canoas.trashbusters.domain.inimigo;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.domain.logCombate.LogCombate;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "inimigo")
public class Inimigo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "hp_max", nullable = false)
    private Integer hpMax;

    @Column(name = "hp_atual", nullable = false)
    private Integer hpAtual;

    @ManyToOne
    @JoinColumn(name = "bairro_id")
    private Bairro bairro;

    @JoinColumn(name = "status_id")
    private int statusId;

    @OneToMany(mappedBy = "inimigo")
    private List<DenunciaLixo> denunciaLixoList = new ArrayList<>();

    @OneToMany(mappedBy = "inimigo")
    private List<LogCombate> logCombateList = new ArrayList<>();
}