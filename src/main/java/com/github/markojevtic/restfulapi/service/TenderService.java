package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.entity.Tender;

import java.util.List;

/**
 * Service contains all necessary logic for manipulating Tenders.
 */
public interface TenderService {
    /**
     * Validates given tender, and store it.
     *
     * @param tender - tender to be created.
     * @return - stored tender.
     * @throws IllegalArgumentException if tender object is not valid.
     */
    Tender createNewTender(Tender tender);

    /**
     * Load all entities and filter it by issuerId.
     *
     * @param issuerId - issuerId or empty/null
     * @return all entities or filtered by issuerId if it not empty.
     */
    List<Tender> findAllAndFilterByIssuer(String issuerId);

    /**
     * Methods does check if tender with given id is open for biding.
     *
     * @param tenderId - id of tender that we ask for bidding.
     * @return true if tender is biddable otherwise false.
     */
    Boolean isTenderBiddable(String tenderId);

    /**
     * Method change status of tender with given id to Closed, it should make the tender non biddable.
     *
     * @param tenderId - target tender id.
     * @throws IllegalArgumentException if the tender is not open or it doesn't exist.
     */
    void closeTender(String tenderId);
}
