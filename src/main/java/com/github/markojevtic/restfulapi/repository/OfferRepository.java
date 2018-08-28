package com.github.markojevtic.restfulapi.repository;

import com.github.markojevtic.restfulapi.repository.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, String> {

    List<Offer> findByTenderId(String tenderId);

    List<Offer> findByBidderId(String bidderId);

    List<Offer> findByTenderIdAndBidderId(String tenderId, String bidderId);
}
