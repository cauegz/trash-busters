package br.gov.rs.canoas.trashbusters.services;

import br.gov.rs.canoas.trashbusters.domain.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    public String storeImage(Path temporaryImage, String originalFilename) {
        try {
            User usuario = getUsuarioLogado();
            Path pastaUploads = Paths.get(
                    System.getProperty("user.home"), "trash-busters", "uploads",
                    usuario.getId().toString());
            Files.createDirectories(pastaUploads);

            String nomeUnico = UUID.randomUUID() + "_" + sanitizeFilename(originalFilename) + ".jpg";
            Path caminhoCompleto = pastaUploads.resolve(nomeUnico);
            Files.move(temporaryImage, caminhoCompleto, StandardCopyOption.REPLACE_EXISTING);
            return caminhoCompleto.toString();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao tentar salvar a imagem: " + e.getMessage(), e);
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "imagem";
        }
        String baseName = Paths.get(filename).getFileName().toString();
        return baseName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private User getUsuarioLogado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assert auth != null;
        return (User) auth.getPrincipal();
    }
}
