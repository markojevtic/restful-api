package com.github.markojevtic.restfulapi.resource.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class TenderDto extends ResourceSupport {

    private String tenderId;
    private String issuerId;
    private String description;

    public static class TenderDtoBuilder extends LombokDtoBuilder<TenderDto> {
        //Will be created by lombok
    }
}
