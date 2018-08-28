package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.entity.Offer;

import java.util.List;

/**
 * Take care about basic offer operations: creating and querying Offers.
 */
public interface OfferService {
    /**
     * Validates given offer and if it's valid store it into repository.
     *
     * @param offer offer to be validated.
     * @return stored offer with initialized fields.
     */
    Offer createOffer(Offer offer);

    List<Offer> findByTenderId(String tenderId);

    List<Offer> findByBidderId(String bidderId);

    List<Offer> findByTenderIdAndBidderId(String tenderId, String bidderId);

    /**
     * Accept offer for the offerId, and decline all other offer for the same tender. In case that offer
     * cannot be accepted throws IllegalArgumentException.
     *
     * @param offerId the id of offer that has to be accepted.
     * @throws IllegalArgumentException in case that given id doesn't refer to a offer that can be accepted.
     */
    void acceptOffer(String offerId);
}
