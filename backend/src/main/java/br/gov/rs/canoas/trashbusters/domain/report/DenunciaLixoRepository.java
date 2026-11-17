package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DenunciaLixoRepository extends JpaRepository<DenunciaLixo, Long> {
    List<DenunciaLixo> findByUsuario(User usuario);

    @Query(value = """
        SELECT * FROM denuncia_lixo
        WHERE ST_DWithin(
            CAST(coordenadas AS geography),
            CAST(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326) AS geography),
            :raio
        )
        """, nativeQuery = true)
    List<DenunciaLixo> findByRaio(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("raio") double raio
    );

    @Query(value = """
        SELECT d.* FROM denuncia_lixo d
        INNER JOIN bairro b ON ST_Contains(b.poligono, d.coordenadas)
        WHERE b.id = :bairroId
        """, nativeQuery = true)
    List<DenunciaLixo> findByBairro(@Param("bairroId") Long bairroId);
}