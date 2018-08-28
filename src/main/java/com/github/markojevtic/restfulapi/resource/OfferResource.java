package com.github.markojevtic.restfulapi.resource;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import com.github.markojevtic.restfulapi.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.OFFER_DTO_LIST_DESCRIPTOR;
import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.OFFER_LIST_DESCRIPTOR;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/offers", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
public class OfferResource {
    @Autowired
    private OfferService offerService;

    @Autowired
    private ConversionService conversionService;

    public static final ControllerLinkBuilder createLink() {
        return linkTo(OfferResource.class);
    }

    public static final ControllerLinkBuilder createLinkToQueryByTenderId(String tenderId) {
        return linkTo(methodOn(OfferResource.class).getAllByTenderId(tenderId));
    }

    public static final ControllerLinkBuilder createLinkToAcceptOffer(String offerId) {
        return linkTo(methodOn(OfferResource.class).acceptOffer(offerId));
    }

    public static final ControllerLinkBuilder createLinkToQueryByBidderId(String bidderId) {
        return linkTo(methodOn(OfferResource.class).getAllByBidderId(bidderId));
    }

    public static final ControllerLinkBuilder createLinkToQueryByTenderIdAndBidderId(String tenderId, String bidderId) {
        return linkTo(methodOn(OfferResource.class).getAllByTenderIdAndBidderId(tenderId, bidderId));
    }

    @PostMapping
    public ResponseEntity<OfferDto> createOffer(@RequestBody OfferDto offerDto) {
        Offer newOffer = offerService.createOffer(conversionService.convert(offerDto, Offer.class));
        return ResponseEntity.status(CREATED).body(
                conversionService.convert(newOffer, OfferDto.class)
        );
    }

    @GetMapping(params = "tenderId")
    public ResponseEntity<List<TenderDto>> getAllByTenderId(@RequestParam String tenderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByTenderId(tenderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @GetMapping(params = "bidderId")
    public ResponseEntity<List<TenderDto>> getAllByBidderId(@RequestParam String bidderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByBidderId(bidderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @GetMapping(params = {"tenderId", "bidderId"})
    public ResponseEntity<List<TenderDto>> getAllByTenderIdAndBidderId(@RequestParam String tenderId, @RequestParam String bidderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByTenderIdAndBidderId(tenderId, bidderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @PostMapping(path = "/{offerId}/accepted")
    public ResponseEntity<Void> acceptOffer(@PathVariable String offerId) {
        offerService.acceptOffer(offerId);
        return ResponseEntity.noContent().build();
    }
}
