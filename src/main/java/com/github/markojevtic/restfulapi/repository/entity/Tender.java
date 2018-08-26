package com.github.markojevtic.restfulapi.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Tender {
    @Id
    @Column(length = 36)
    private String tenderId;

    @Column(length = 36)
    private String issuerId;

    @Column(length = 500)
    private String description;
}
