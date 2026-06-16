package com.awesomepizza.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SizeConverter implements AttributeConverter<Size, String> {

    @Override
    public String convertToDatabaseColumn(Size size) {
        return size != null ? size.getName() : null;
    }

    @Override
    public Size convertToEntityAttribute(String name) {
        return name != null ? Size.valueOf(name) : null;
    }
}
