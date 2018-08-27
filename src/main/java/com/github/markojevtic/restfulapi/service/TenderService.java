package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.entity.Tender;

import java.util.List;

/**
 * Service contains all necessary logic for manipulating Tenders.
 */
public interface TenderService {
    /**
     * Validates given tender, and store it.
     * @param tender - tender to be created.
     * @return - stored tender.
     * @throws IllegalArgumentException if tender object is not valid.
     */
    Tender createNewTender(Tender tender);

    /**
     * Load all entities and filter it by issuerId.
     * @param issuerId - issuerId or empty/null
     * @return all entities or filtered by filterId if it has issuerId.
     */
    List<Tender> findAllAndFilterByIssuer(String issuerId);

    /**
     * Methods does check if tender with given id is open for biding.
     * @param tenderId - id of tender that we ask for bidding.
     * @return true if tender is biddable otherwise false.
     */
    Boolean isTenderBiddable(String tenderId);
}
