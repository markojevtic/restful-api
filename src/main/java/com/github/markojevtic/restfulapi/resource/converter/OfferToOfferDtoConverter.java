package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.resource.OfferResource;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class OfferToOfferDtoConverter implements Converter<Offer, OfferDto> {

    @Override
    public OfferDto convert(Offer source) {
        return OfferDto.builder()
                .offerId(source.getOfferId())
                .tenderId(source.getTenderId())
                .bidderId(source.getBidderId())
                .description(source.getDescription())
                .buildWithLinks(
                        OfferResource.createLinkToQueryByTenderId(source.getTenderId()).withRel("tenderOffers"),
                        OfferResource.createLinkToQueryByTenderId(source.getTenderId()).withRel("bidderOffers"),
                        OfferResource.createLinkToQueryByTenderIdAndBidderId(source.getTenderId(), source.getBidderId()).withRel("tenderAndBidderOffers")
                );
    }
}
