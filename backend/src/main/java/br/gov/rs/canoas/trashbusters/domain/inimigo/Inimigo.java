package br.gov.rs.canoas.trashbusters.domain.inimigo;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;
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

    @Column(name = "status_id", nullable = false)
    private StatusInimigo status;

    @OneToMany(mappedBy = "inimigo")
    private List<DenunciaLixo> denunciaLixoList = new ArrayList<>();

    public void receberDano(Integer dano, StatusInimigo statusDerrotado) {
        this.hpAtual = Math.max(0, this.hpAtual - dano);

        if(hpAtual == 0){
            this.status = statusDerrotado;
        }
    }
}