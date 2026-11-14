package br.gov.rs.canoas.trashbusters.domain.logCombate;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.report.DenunciaLixo;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "log_combate")
public class LogCombate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "dano_realizado")
    private Integer danoRealizado;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;
    @ManyToOne
    @JoinColumn(name = "denuncia_lixo_id")
    private DenunciaLixo denunciaLixo;
    @ManyToOne
    @JoinColumn(name = "inimigo_id")
    private Inimigo inimigo;
}