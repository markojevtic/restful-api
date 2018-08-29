package com.github.markojevtic.restfulapi.service.impl;

import com.github.markojevtic.restfulapi.repository.OfferRepository;
import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
import com.github.markojevtic.restfulapi.service.OfferService;
import com.github.markojevtic.restfulapi.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private OfferRepository repository;

    @Autowired
    private TenderService tenderService;

    @Override
    public Offer createOffer(Offer offer) {
        Assert.isTrue(tenderService.isTenderBiddable(offer.getTenderId()), "There is no tender open for bidding!");
        Assert.hasText(offer.getBidderId(), "Offer must have a valid bidder id!");

        offer.setOfferId(UUID.randomUUID().toString());
        offer.setStatus(OfferStatus.NEW);
        return repository.save(offer);
    }


    @Override
    public List<Offer> findAllAndFilterByTenderIdAndBidderId(String tenderId, String bidderId) {
        if( shouldFilterBy(tenderId) && shouldFilterBy(bidderId)) {
            return repository.findByTenderIdAndBidderId(tenderId, bidderId);
        } else if(shouldFilterBy(tenderId)) {
            return repository.findByTenderId(tenderId);
        } else if(shouldFilterBy(bidderId)){
            return repository.findByBidderId(bidderId);
        } else {
            return repository.findAll();
        }

    }

    private boolean shouldFilterBy(String param) {
        return StringUtils.hasText(param);
    }

    @Override
    @Transactional
    public void acceptOffer(String offerId) {
        Offer acceptedOffer = repository.findById(offerId)
                .orElseThrow( () -> new IllegalArgumentException("OfferId refers to non existing offer!"));
        Assert.isTrue(acceptedOffer.getStatus() == OfferStatus.NEW, "Target offer must have status NEW!");
        acceptedOffer.setStatus(OfferStatus.ACCEPTED);
        List<Offer> declinedOffers = repository.findByTenderId(acceptedOffer.getTenderId()).stream()
                .filter( offer -> !offerId.equals(offer.getOfferId()))
                .map( offer -> {
                            offer.setStatus(OfferStatus.DECLINED);
                            return offer;
                        })
                .collect(Collectors.toList());
        repository.save(acceptedOffer);
        repository.saveAll(declinedOffers);

        tenderService.closeTender(acceptedOffer.getTenderId());
    }
}
