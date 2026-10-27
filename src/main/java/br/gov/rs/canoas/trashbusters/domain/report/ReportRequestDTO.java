package br.gov.rs.canoas.trashbusters.domain.report;

import org.springframework.web.multipart.MultipartFile;

public record ReportRequestDTO(
        String endereco,
        Integer inimigoId,
        Integer bairroId,
        MultipartFile imagem
){}
