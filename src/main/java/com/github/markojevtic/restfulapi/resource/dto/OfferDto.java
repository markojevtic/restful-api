package com.github.markojevtic.restfulapi.resource.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class OfferDto extends ResourceSupport {

    private String offerId;
    private String tenderId;
    private String bidderId;
    private String description;

    public static class OfferDtoBuilder extends LombokDtoBuilder<OfferDto> {
        //Will be created by lombok
    }
}
