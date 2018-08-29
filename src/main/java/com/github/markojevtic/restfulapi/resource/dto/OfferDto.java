package com.github.markojevtic.restfulapi.resource.dto;

import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

import javax.validation.constraints.Size;

@Builder
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(description = "Represents a offer for tender.")
public class OfferDto extends ResourceSupport {

    @Size(max = 36)
    @ApiModelProperty( notes = "The unique identifier of offer, it's auto-generated by application.(Max length 36)", readOnly = true, example = "Read-only, in post will be ignored.")
    private String offerId;

    @Size(max = 36)
    @ApiModelProperty( notes = "The id of tender on which bidder applying.(Max length 36)", example = "AN-GENERATE-UUID")
    private String tenderId;

    @Size(max = 36)
    @ApiModelProperty( notes = "The id of bidder.(Max length 36)", example = "max.bidder")
    private String bidderId;

    @Size(max = 500)
    @ApiModelProperty( notes = "Description of the offer.(Max length 500)", example = "I will do it.")
    private String description;

    @ApiModelProperty( notes = "Status of the offer.", readOnly = true)
    private OfferStatus status;

    public static class OfferDtoBuilder extends LombokDtoBuilder<OfferDto> {
        //Will be created by lombok
    }
}
