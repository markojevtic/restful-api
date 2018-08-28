package com.github.markojevtic.restfulapi.resource.dto;

import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
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
    private OfferStatus status;

    public static class OfferDtoBuilder extends LombokDtoBuilder<OfferDto> {
        //Will be created by lombok
    }
}
