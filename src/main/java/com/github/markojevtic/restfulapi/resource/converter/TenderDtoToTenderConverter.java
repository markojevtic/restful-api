package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TenderDtoToTenderConverter implements Converter<TenderDto, Tender> {

    @Override
    public Tender convert(TenderDto source) {
        return Tender.builder()
                .tenderId(source.getTenderId())
                .issuerId(source.getIssuerId())
                .description(source.getDescription())
                .build();
    }
}
