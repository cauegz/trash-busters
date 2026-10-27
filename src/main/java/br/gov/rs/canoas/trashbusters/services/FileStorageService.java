package br.gov.rs.canoas.trashbusters.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    public String storeImage(MultipartFile imagem){
        String diretorio = "uploads/";
        try{
            String nomeOriginal = imagem.getOriginalFilename();
            String nomeUnico = UUID.randomUUID() + "_" + nomeOriginal;

            Path caminhoCopmleto = Paths.get(diretorio).resolve(nomeUnico);

            imagem.transferTo(caminhoCopmleto.toFile());

            return caminhoCopmleto.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar salvar a imagem: " + e.getMessage(), e);
        }
    }
}
