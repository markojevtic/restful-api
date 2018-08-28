package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.entity.Offer;

import java.util.List;

/**
 * Take care about basic offer operations: creating and querying Offers.
 */
public interface OfferService {
    /**
     * Validates given offer and if it's valide store it into reposiotry.
     *
     * @param offer offer to be validated.
     * @return stored offer with initialized fields.
     */
    Offer createOffer(Offer offer);

    List<Offer> findByTenderId(String tenderId);

    List<Offer> findByBidderId(String bidderId);

    List<Offer> findByTenderIdAndBidderId(String tenderId, String bidderId);
}
