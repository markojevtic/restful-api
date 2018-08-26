package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TenderToTenderDtoConverter implements Converter<Tender, TenderDto> {

    @Override
    public TenderDto convert(Tender source) {
        return TenderDto.builder()
                .tenderId(source.getTenderId())
                .issuerId(source.getIssuerId())
                .description(source.getDescription())
                .build();
    }
}
