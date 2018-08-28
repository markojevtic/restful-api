package com.github.markojevtic.restfulapi.service.impl;

import com.github.markojevtic.restfulapi.repository.TenderRepository;
import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.repository.entity.TenderStatus;
import com.github.markojevtic.restfulapi.service.TenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenderServiceImpl implements TenderService {
    @Autowired
    private TenderRepository repository;

    @Override
    public Tender createNewTender(Tender tender) {
        Assert.hasText(tender.getIssuerId(), "Issuer id must not be empty!");
        tender.setTenderId(UUID.randomUUID().toString());
        tender.setStatus(TenderStatus.OPEN);
        return repository.save(tender);
    }

    @Override
    public List<Tender> findAllAndFilterByIssuer(String issuerId) {
        if (StringUtils.isEmpty(issuerId)) {
            return repository.findAll();
        } else {
            return repository.findByIssuerId(issuerId);
        }
    }

    @Override
    public Boolean isTenderBiddable(String tenderId) {
        Optional<Tender> tender = repository.findById(tenderId);
        return tender.isPresent() && tender.get().getStatus() == TenderStatus.OPEN;
    }

    @Override
    @Transactional
    public void closeTender(String tenderId) {
        Tender tender = repository.findById(tenderId)
                .orElseThrow(() -> new IllegalArgumentException("Is not possible to close a non existing Tender."));
        Assert.isTrue(tender.getStatus() == TenderStatus.OPEN, "It's only possible to close an open Tender.");
        tender.setStatus(TenderStatus.CLOSE);
        repository.save(tender);
    }
}
