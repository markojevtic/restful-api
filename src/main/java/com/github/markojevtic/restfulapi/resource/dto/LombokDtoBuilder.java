package com.github.markojevtic.restfulapi.resource.dto;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

public abstract class LombokDtoBuilder<T extends ResourceSupport> {
    public abstract T build();

    public T buildWithLinks(Link... links) {
        T result = build();
        result.add(links);
        return result;
    }
}
