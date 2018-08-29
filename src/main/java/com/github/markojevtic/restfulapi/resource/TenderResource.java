package com.github.markojevtic.restfulapi.resource;

import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import com.github.markojevtic.restfulapi.service.TenderService;
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
@RequestMapping(value = "/tenders", produces = APPLICATION_JSON_UTF8_VALUE, consumes = APPLICATION_JSON_UTF8_VALUE)
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

    @PostMapping
    public ResponseEntity<TenderDto> createTender(@Valid @RequestBody TenderDto tenderDto) {
        Tender newTender = tenderService.createNewTender(conversionService.convert(tenderDto, Tender.class));
        return ResponseEntity.status(CREATED).body(
                conversionService.convert(newTender, TenderDto.class)
        );
    }

    @SuppressWarnings("unchecked")
    @GetMapping
    public ResponseEntity<List<TenderDto>> getTenderByIssuer(@RequestParam String issuerId) {
        return ResponseEntity.ok(
                (List<TenderDto>) conversionService.convert(tenderService.findAllAndFilterByIssuer(issuerId), TENDER_LIST_DESCRIPTOR, TENDER_DTO_LIST_DESCRIPTOR)
        );
    }
}
