package com.github.markojevtic.restfulapi.resource.converter;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

public class ConversionTypeDescriptors {
    public static final TypeDescriptor TENDER_DTO_LIST_DESCRIPTOR = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(TenderDto.class));
    public static final TypeDescriptor TENDER_LIST_DESCRIPTOR = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Tender.class));
    public static final TypeDescriptor OFFER_DTO_LIST_DESCRIPTOR = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(OfferDto.class));
    public static final TypeDescriptor OFFER_LIST_DESCRIPTOR = TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(Offer.class));
    private ConversionTypeDescriptors() {
    }

}
