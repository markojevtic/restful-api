package com.github.markojevtic.restfulapi.resource.dto;

import com.github.markojevtic.restfulapi.repository.entity.TenderStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.Size;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
public class TenderDto extends ResourceSupport {

    @Size(max = 36)
    private String tenderId;

    @Size(max = 36)
    private String issuerId;

    @Size(max = 500)
    private String description;

    private TenderStatus status;

    public static class TenderDtoBuilder extends LombokDtoBuilder<TenderDto> {
        //Will be created by lombok
    }
}
