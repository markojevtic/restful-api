package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

public class ConversionTypeDescriptors {
    private ConversionTypeDescriptors() {
    }

    public static final TypeDescriptor TENDER_DTO_LIST_DESCRIPTOR = TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( TenderDto.class ) );
    public static final TypeDescriptor TENDER_LIST_DESCRIPTOR = TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Tender.class ) );

}
