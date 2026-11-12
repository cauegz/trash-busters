package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            User usuario = getUsuarioLogado();

            Path pastaUploads = Paths.get(
                    System.getProperty("user.home"),
                    "trash-busters",
                    "uploads",
                    usuario.getId().toString()
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

    private User getUsuarioLogado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        return (User) auth.getPrincipal();
    }
}
