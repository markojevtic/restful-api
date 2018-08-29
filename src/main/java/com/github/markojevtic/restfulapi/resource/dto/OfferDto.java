package com.github.markojevtic.restfulapi.resource.dto;

import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.Size;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class OfferDto extends ResourceSupport {

    @Size(max = 36)
    private String offerId;

    @Size(max = 36)
    private String tenderId;

    @Size(max = 36)
    private String bidderId;

    @Size(max = 500)
    private String description;

    private OfferStatus status;

    public static class OfferDtoBuilder extends LombokDtoBuilder<OfferDto> {
        //Will be created by lombok
    }
}
