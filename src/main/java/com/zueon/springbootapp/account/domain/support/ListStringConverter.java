package com.zueon.springbootapp.account.domain.support;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Converter      // converter로 사용할 클래스임을 나타낸다.
public class ListStringConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return Optional.ofNullable(attribute)
                .filter(list -> !list.isEmpty())        // 비어있을 때 아무것도 하지 않도록 한다.
                .map(a -> String.join(",",a ))
                .orElse(null);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return Collections.emptyList();
        }
        return Stream.of(dbData.split(",")).collect(Collectors.toList());
    }
}
