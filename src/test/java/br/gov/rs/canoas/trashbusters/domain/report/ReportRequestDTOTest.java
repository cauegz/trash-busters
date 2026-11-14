package br.gov.rs.canoas.trashbusters.domain.report;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportRequestDTOTest {
    private static Validator validator;
    private static jakarta.validation.ValidatorFactory validatorFactory;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void deveAceitarDenunciaComDadosValidos() {
        ReportRequestDTO request = new ReportRequestDTO(
                "Rua Exemplo, 100",
                -51.1837,
                -29.9178,
                imagem()
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void deveRejeitarDadosObrigatoriosAusentesECoordenadasInvalidas() {
        ReportRequestDTO request = new ReportRequestDTO("", 181.0, -91.0, imagemVazia());

        Set<?> violations = validator.validate(request);

        assertTrue(violations.size() >= 4);
    }

    private static MockMultipartFile imagem() {
        return new MockMultipartFile("imagem", "lixo.jpg", "image/jpeg", new byte[]{1});
    }

    private static MockMultipartFile imagemVazia() {
        return new MockMultipartFile("imagem", "lixo.jpg", "image/jpeg", new byte[0]);
    }
}
