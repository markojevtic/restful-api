package com.github.markojevtic.restfulapi.resource;

import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import com.github.markojevtic.restfulapi.service.TenderService;
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

import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.TENDER_DTO_LIST_DESCRIPTOR;
import static com.github.markojevtic.restfulapi.resource.converter.ConversionTypeDescriptors.TENDER_LIST_DESCRIPTOR;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(value = "/tenders", produces = APPLICATION_JSON_UTF8_VALUE)
@Api(description = "API for creating and querying Tenders.")
public class TenderResource {
    @Autowired
    private TenderService tenderService;

    @Autowired
    private ConversionService conversionService;

    public static ControllerLinkBuilder createLink() {
        return linkTo(TenderResource.class);
    }

    public static ControllerLinkBuilder createLinkToQueryByIssuerId(String issuerId) {
        return linkTo(methodOn(TenderResource.class).getTenderByIssuer(issuerId));
    }

    @ApiOperation("Handles creation of new tender in system. It does validation of input tender, and initialization of read-only fields.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tender has been created successfully."),
            @ApiResponse(code = 400, message = "Posted tender is not valid."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<TenderDto> createTender(@Valid @RequestBody TenderDto tenderDto) {
        Tender newTender = tenderService.createNewTender(conversionService.convert(tenderDto, Tender.class));
        return ResponseEntity.status(CREATED).body(
                conversionService.convert(newTender, TenderDto.class)
        );
    }

    @ApiOperation("Querying tenders and perform filter by issuer id if it's presented, otherwise returning all tenders.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successful, result is a list of tenders."),
            @ApiResponse(code = 500, message = "An unexpected server error")
    })
    @GetMapping
    public ResponseEntity<List<TenderDto>> getTenderByIssuer(@RequestParam(required = false) String issuerId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(tenderService.findAllAndFilterByIssuer(issuerId), TENDER_LIST_DESCRIPTOR, TENDER_DTO_LIST_DESCRIPTOR)
        );
    }
}
