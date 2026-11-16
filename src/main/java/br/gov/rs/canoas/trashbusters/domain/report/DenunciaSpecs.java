package br.gov.rs.canoas.trashbusters.domain.report;

import br.gov.rs.canoas.trashbusters.domain.status.StatusDenuncia;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DenunciaSpecs {

    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public static Specification<DenunciaLixo> doBairro(Integer bairroId) {
        return (root, query, builder) -> {
            if (bairroId == null) return null;
            return builder.equal(root.get("inimigo").get("bairro").get("id"), bairroId);
        };
    }

    public static Specification<DenunciaLixo> doInimigo(Integer inimigoId) {
        return (root, query, builder) -> {
            if (inimigoId == null) return null;
            return builder.equal(root.get("inimigo").get("id"), inimigoId);
        };
    }

    public static Specification<DenunciaLixo> comStatus(StatusDenuncia status) {
        return (root, query, builder) -> {
            if (status == null) return null;
            return builder.equal(root.get("status"), status);
        };
    }

    public static Specification<DenunciaLixo> comCategoria(CategoriaLixo categoria) {
        return (root, query, builder) -> {
            if (categoria == null) return null;
            return builder.equal(root.get("categoria"), categoria);
        };
    }

    public static Specification<DenunciaLixo> comSeveridadeMinima(Integer severidadeMinima) {
        return (root, query, builder) -> {
            if (severidadeMinima == null) return null;
            return builder.greaterThanOrEqualTo(root.get("severidade"), severidadeMinima);
        };
    }

    public static Specification<DenunciaLixo> proximoDe(Double latitude, Double longitude, Double raioMetros) {
        return (root, query, builder) -> {
            if (latitude == null || longitude == null || raioMetros == null) return null;

            Point pontoBusca = geometryFactory.createPoint(new Coordinate(longitude, latitude));

            return builder.isTrue(
                    builder.function(
                            "ST_DWithin",
                            Boolean.class,
                            builder.function("geography", Object.class, root.get("coordenadas")),
                            builder.function("geography", Object.class, builder.literal(pontoBusca)),
                            builder.literal(raioMetros)
                    )
            );
        };
    }

    public static Specification<DenunciaLixo> criadoEntre(LocalDateTime inicio, LocalDateTime fim) {
        return (root, query, builder) -> {
            if (inicio == null && fim == null) return null;
            if (inicio != null && fim != null) return builder.between(root.get("criadoEm"), inicio, fim);
            if (inicio != null) return builder.greaterThanOrEqualTo(root.get("criadoEm"), inicio);
            return builder.lessThanOrEqualTo(root.get("criadoEm"), fim);
        };
    }
}