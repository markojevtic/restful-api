package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OfferDtoToOfferConverter implements Converter<OfferDto, Offer> {

    @Override
    public Offer convert(OfferDto source) {
        return Offer.builder()
                .offerId(source.getOfferId())
                .tenderId(source.getTenderId())
                .bidderId(source.getBidderId())
                .description(source.getDescription())
                .build();
    }
}
