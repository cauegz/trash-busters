package br.gov.rs.canoas.trashbusters.domain.status;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusInimigoConverter implements AttributeConverter<StatusInimigo, Long> {

    @Override
    public Long convertToDatabaseColumn(StatusInimigo status) {
        return status != null ? status.getId() : null;
    }

    @Override
    public StatusInimigo convertToEntityAttribute(Long dbData) {
        return dbData != null ? StatusInimigo.fromId(dbData) : null;
    }
}
