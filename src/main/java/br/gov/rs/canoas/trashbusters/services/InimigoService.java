package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InimigoService {
    private final InimigoRepository inimigoRepository;

    public InimigoService(InimigoRepository inimigoRepository) {
        this.inimigoRepository = inimigoRepository;
    }

    public Optional<Inimigo> getInimigoById(long idInimigo){
        return this.inimigoRepository.findById(idInimigo);
    }

    public Optional<Inimigo> findByBairroId(long bairroId){
        return this.inimigoRepository.findByBairroId(bairroId);
    }
}
