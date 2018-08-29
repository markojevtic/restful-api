package com.github.markojevtic.restfulapi.resource;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import com.github.markojevtic.restfulapi.service.OfferService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.OFFER_DTO_LIST_DESCRIPTOR;
import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.OFFER_LIST_DESCRIPTOR;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/offers", produces = APPLICATION_JSON_UTF8_VALUE)
@Api(description = "API for creating, querying and accepting Offers.")
public class OfferResource {
    @Autowired
    private OfferService offerService;

    @Autowired
    private ConversionService conversionService;

    public static ControllerLinkBuilder createLink() {
        return linkTo(OfferResource.class);
    }

    public static ControllerLinkBuilder createLinkToQueryByTenderId(String tenderId) {
        return linkTo(methodOn(OfferResource.class).getAllByTenderId(tenderId));
    }

    public static ControllerLinkBuilder createLinkToAcceptOffer(String offerId) {
        return linkTo(methodOn(OfferResource.class).acceptOffer(offerId));
    }

    public static ControllerLinkBuilder createLinkToQueryByBidderId(String bidderId) {
        return linkTo(methodOn(OfferResource.class).getAllByBidderId(bidderId));
    }

    public static ControllerLinkBuilder createLinkToQueryByTenderIdAndBidderId(String tenderId, String bidderId) {
        return linkTo(methodOn(OfferResource.class).getAllByTenderIdAndBidderId(tenderId, bidderId));
    }

    @ApiOperation("Handles creation of new offer in system. It does validation of input offer, initialization of read-only fields.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Offer has been created successfully."),
            @ApiResponse(code = 400, message = "Posted offer is not valid. Take a look into payload for the reason."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OfferDto> createOffer(@Valid @RequestBody OfferDto offerDto) {
        Offer newOffer = offerService.createOffer(conversionService.convert(offerDto, Offer.class));
        return ResponseEntity.status(CREATED).body(
                conversionService.convert(newOffer, OfferDto.class)
        );
    }

    @ApiOperation(value = "Querying offers by tender-id.", nickname = "getAllByTenderId")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful, result is a list of offers for given tender-id."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @GetMapping(params = "tenderId")
    public ResponseEntity<List<TenderDto>> getAllByTenderId(@RequestParam String tenderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByTenderId(tenderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @ApiOperation(value = "Querying offers by bidder-id.", nickname = "getAllByBidderId")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful, result is a list of offers for given bidder-id."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @GetMapping(params = "bidderId")
    public ResponseEntity<List<TenderDto>> getAllByBidderId(@RequestParam String bidderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByBidderId(bidderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @ApiOperation(value = "Querying offers by tender-id and bidder-id.", nickname = "getAllByTenderIdAndBidderId")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful, result is a list of offers for given tender-id and bidder-id."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @GetMapping(params = {"tenderId", "bidderId"})
    public ResponseEntity<List<TenderDto>> getAllByTenderIdAndBidderId(@RequestParam String tenderId, @RequestParam String bidderId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(offerService.findByTenderIdAndBidderId(tenderId, bidderId), OFFER_LIST_DESCRIPTOR, OFFER_DTO_LIST_DESCRIPTOR)
        );
    }

    @ApiOperation("Perform a accepting operation for the offer with given id. It accepts the offer, and decline all other offers for the same tender.")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Offer has been accepted successfully."),
            @ApiResponse(code = 400, message = "Posted offer could not be accepted. Take a look into payload for the reason."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @PostMapping(path = "/{offerId}/accepted")
    public ResponseEntity<Void> acceptOffer(@PathVariable String offerId) {
        offerService.acceptOffer(offerId);
        return ResponseEntity.noContent().build();
    }
}
