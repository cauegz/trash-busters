package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.inimigo.Inimigo;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoDTO;
import br.gov.rs.canoas.trashbusters.domain.inimigo.InimigoRepository;
import br.gov.rs.canoas.trashbusters.domain.status.StatusInimigo;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<InimigoDTO> listarTodosAtivos() {
        return inimigoRepository.findByStatus(StatusInimigo.ATIVO)
                .stream()
                .map(InimigoDTO::fromEntity)
                .toList();
    }

    public List<InimigoDTO> listarTodosDerrotados() {
        return inimigoRepository.findByStatus(StatusInimigo.DERROTADO)
                .stream()
                .map(InimigoDTO::fromEntity)
                .toList();
    }
}
