package br.gov.rs.canoas.trashbusters.domain.bairro;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.MultiPolygon;

@Entity
@Table(name = "bairro")
@Data
public class Bairro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(name = "codigo_ibge", length = 20)
    private String codigoIbge;

    @Column(name = "poligono", columnDefinition = "geometry(Polygon, 4326)", nullable = false)
    private MultiPolygon poligono;
}