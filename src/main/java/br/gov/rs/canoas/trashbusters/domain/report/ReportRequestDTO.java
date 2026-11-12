package br.gov.rs.canoas.trashbusters.domain.report;

import org.springframework.web.multipart.MultipartFile;

public record ReportRequestDTO(
        String endereco,
        Integer inimigoId, //depois fazer alguma maracutaia pra pegar isso com base na latitude e longitude
        Double longitude, //x
        Double latitude, //y
        Integer bairroId,
        MultipartFile imagem
){}
