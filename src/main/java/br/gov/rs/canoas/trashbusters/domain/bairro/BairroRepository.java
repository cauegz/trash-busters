package br.gov.rs.canoas.trashbusters.domain.bairro;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BairroRepository extends JpaRepository<Bairro, Long> {

    Optional<Bairro> findByNome(String nome);

    // 1. Busca passando um objeto Point (JTS) via JPQL do Hibernate Spatial
    @Query("SELECT b FROM Bairro b WHERE within(:ponto, b.poligono) = true")
    Optional<Bairro> findByPonto(@Param("ponto") Point ponto);

    // 2. Busca passando latitude e longitude diretamente (Double) via PostGIS nativo
    @Query(value = """
        SELECT * FROM bairro
        WHERE ST_Contains(poligono, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326))
        LIMIT 1
        """, nativeQuery = true)
    Optional<Bairro> findByCoordenadas(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );
}
