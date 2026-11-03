package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.gemini.GeminiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.ai.content.Media;

@Service
public class GeminiService {

    public GeminiResponseDTO analisarImagem(Media imagem){

        return new GeminiResponseDTO(true, 1);
    }
}
