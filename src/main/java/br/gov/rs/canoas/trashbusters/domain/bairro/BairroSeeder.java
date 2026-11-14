package br.gov.rs.canoas.trashbusters.domain.bairro;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class BairroSeeder implements CommandLineRunner {

    private final BairroRepository bairroRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String @NonNull ... args) throws Exception {
        // 1. Popula os 18 bairros reais a partir do GeoJSON se a tabela estiver vazia
        if (bairroRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("data/bairros.geojson");
            ObjectMapper objectMapper = new ObjectMapper();
            GeoJsonReader geoJsonReader = new GeoJsonReader();
            GeometryFactory geometryFactory = new GeometryFactory();

            try (InputStream is = resource.getInputStream()) {
                JsonNode features = objectMapper.readTree(is).get("features");

                for (JsonNode feature : features) {
                    if ("Polygon".equals(feature.path("geometry").path("type").asString())) {
                        String nome = feature.path("properties").path("name").asString();
                        String geometryJson = feature.get("geometry").toString();

                        Geometry geom = geoJsonReader.read(geometryJson);
                        MultiPolygon multiPolygon;

                        if (geom instanceof Polygon polygon) {
                            multiPolygon = geometryFactory.createMultiPolygon(new Polygon[]{polygon});
                        } else {
                            multiPolygon = (MultiPolygon) geom;
                        }
                        multiPolygon.setSRID(4326);

                        Bairro bairro = new Bairro();
                        bairro.setNome(nome);
                        bairro.setPoligono(multiPolygon);

                        bairroRepository.save(bairro);
                    }
                }
            }
        }

        // 2. Insere os Bosses vinculados aos bairros reais recém-criados
        jdbcTemplate.execute("""
            INSERT INTO inimigo (nome, hp_max, hp_atual, bairro_id, status_id) VALUES
               ('EntulhoSauro', 1500, 1500, (SELECT id FROM bairro WHERE nome = 'Mathias Velho'), (SELECT id FROM status WHERE nome = 'ATIVO')),
               ('Monstro do Plástico', 2000, 2000, (SELECT id FROM bairro WHERE nome = 'Guajuviras'), (SELECT id FROM status WHERE nome = 'ATIVO')),
               ('Kraken dos Entulhos', 1200, 1200, (SELECT id FROM bairro WHERE nome = 'Niterói'), (SELECT id FROM status WHERE nome = 'ATIVO')),
               ('Lord Pneu', 1000, 1000, (SELECT id FROM bairro WHERE nome = 'Centro'), (SELECT id FROM status WHERE nome = 'ATIVO')),
               ('Fumaça Negra', 1300, 1300, (SELECT id FROM bairro WHERE nome = 'Harmonia'), (SELECT id FROM status WHERE nome = 'ATIVO'))
            ON CONFLICT (nome) DO NOTHING;
        """);
    }
}