package com.github.markojevtic.restfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.github.markojevtic.restfulapi.repository.entity.Tender;

import java.util.List;

public interface TenderRepository extends JpaRepository<Tender, String> {
    List<Tender> findByIssuerId(String issuerId);
}
