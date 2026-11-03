package br.gov.rs.canoas.trashbusters.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    public String storeImage(MultipartFile imagem){
        try{
            Path pastaUploads = Paths.get(
                    System.getProperty("user.home"),
                    "trash-busters",
                    "uploads"
            );

            String nomeOriginal = imagem.getOriginalFilename();
            String nomeUnico = UUID.randomUUID() + "_" + nomeOriginal;

            Files.createDirectories(pastaUploads);

            Path caminhoCopmleto = pastaUploads.resolve(nomeUnico);

            imagem.transferTo(caminhoCopmleto.toFile());

            return caminhoCopmleto.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar salvar a imagem: " + e.getMessage(), e);
        }
    }
}
