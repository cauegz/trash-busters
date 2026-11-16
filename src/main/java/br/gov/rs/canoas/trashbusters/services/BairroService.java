package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.domain.bairro.BairroDTO;
import br.gov.rs.canoas.trashbusters.domain.bairro.BairroRepository;
import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoRepository;
import br.gov.rs.canoas.trashbusters.infra.exception.BairroNotFoundException;
import br.gov.rs.canoas.trashbusters.infra.exception.InimigoNotFoundException;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BairroService {
    private final BairroRepository bairroRepository;
    private final InimigoRepository inimigoRepository;

    public BairroService(BairroRepository bairroRepository, InimigoRepository inimigoRepository) {
        this.bairroRepository = bairroRepository;
        this.inimigoRepository = inimigoRepository;
    }

    public Optional<Bairro> findByPonto(Point coordenadas){
        return this.bairroRepository.findByPonto(coordenadas);
    }

    public List<BairroDTO> findAll() {
        List<BairroDTO> bairrosDTO = new ArrayList<>();

        List<Inimigo> inimigos = inimigoRepository.findAll();
        List<Bairro> bairros = bairroRepository.findAll();

        for(Bairro bairro : bairros){
            Inimigo inimigo =
                    inimigos.stream()
                            .filter(inimigo1 -> inimigo1.getBairro().getId().equals(bairro.getId()))
                            .findFirst()
                            .orElseThrow(() -> new InimigoNotFoundException(0L));

            bairrosDTO.add(BairroDTO.fromEntity(bairro, inimigo));
        }

        return bairrosDTO;
    }

    public BairroDTO findById(Long id) {
        Bairro bairro = bairroRepository.findById(id)
                .orElseThrow(() -> new BairroNotFoundException(id));

        Inimigo inimigo = inimigoRepository.findByBairroId(id)
                .orElseThrow(() -> new InimigoNotFoundException(id));

        return BairroDTO.fromEntity(bairro, inimigo);
    }
}
