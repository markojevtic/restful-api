package com.github.markojevtic.restfulapi.resource.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String message;
}
