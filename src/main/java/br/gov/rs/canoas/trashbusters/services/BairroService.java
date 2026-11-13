package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.bairro.Bairro;
import br.gov.rs.canoas.trashbusters.domain.bairro.BairroRepository;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BairroService {
    private final BairroRepository bairroRepository;

    public BairroService(BairroRepository bairroRepository) {
        this.bairroRepository = bairroRepository;
    }

    public Optional<Bairro> findByPonto(Point coordenadas){
        return this.bairroRepository.findByPonto(coordenadas);
    }
}
