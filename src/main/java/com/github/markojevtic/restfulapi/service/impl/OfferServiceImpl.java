package com.github.markojevtic.restfulapi.service.impl;

import com.github.markojevtic.restfulapi.repository.OfferRepository;
import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.service.OfferService;
import com.github.markojevtic.restfulapi.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;

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
        return repository.save(offer);
    }

    @Override
    public List<Offer> findByTenderId(String tenderId) {
        return repository.findByTenderId(tenderId);
    }

    @Override
    public List<Offer> findByBidderId(String bidderId) {
        return repository.findByBidderId(bidderId);
    }

    @Override
    public List<Offer> findByTenderIdAndBidderId(String tenderId, String bidderId) {
        return repository.findByTenderIdAndBidderId(tenderId, bidderId);
    }
}