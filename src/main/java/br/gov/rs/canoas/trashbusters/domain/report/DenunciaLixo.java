package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.logCombate.LogCombate;
import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import br.gov.rs.canoas.trashbusters.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "denuncia_lixo")
public class DenunciaLixo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    // Endereço legível (ex: rua, bairro)
    private String endereco;

    // Ponto espacial do PostGIS (SRID 4326 = GPS)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point coordenadas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDenuncia status;

    private String categoria;

    private Integer severidade;

    @Column(length = 500)
    private String justificativaIa;

    @Column(name = "caminho_imagem")
    private String caminhoImagem;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "inimigo_id")
    private Inimigo inimigo;

    @OneToMany(mappedBy = "denunciaLixo")
    private List<LogCombate> logCombateList = new ArrayList<>();
}