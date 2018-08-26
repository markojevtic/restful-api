package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.TenderRepository;
import com.github.markojevtic.restfulapi.repository.entity.Tender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TenderServiceUnitTest {
    @Autowired
    private TenderService tenderService;

    @MockBean
    private TenderRepository tenderRepository;

    @Test
    public void createNewTenderInitializeIdAndCallRepositorySave() {
        doAnswer(invocation -> invocation.getArguments()[0])
                .when(tenderRepository).save(any(Tender.class));

        Tender testTender = Tender.builder()
                .issuerId("testIssuer")
                .description("Test tender")
                .build();
        assertThat(tenderService.createNewTender(testTender))
                .extracting(Tender::getTenderId).isNotNull();

        verify(tenderRepository, times(1)).save(any(Tender.class));
    }

    @Test
    public void createNewTenderThrowsExceptionWhenIssuerIdIsNotPresented() {
        Tender tenderWithNoIssuer = Tender.builder()
                .description("Tender with no issuer").build();

        assertThatThrownBy(() -> tenderService.createNewTender(tenderWithNoIssuer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Issuer id must not be empty!");
    }

    @Test
    public void findAllAndFilterByIssuerFindsAllWhenFilterIsNullOrEmpty() {

        tenderService.findAllAndFilterByIssuer(null);
        verify(tenderRepository, times(1)).findAll();

        tenderService.findAllAndFilterByIssuer("");
        verify(tenderRepository, times(2)).findAll();

        verify(tenderRepository, never()).findByIssuerId(anyString());
    }

    @Test
    public void findAllAndQueryRepositoryByGivenIssuerId() {

        String testIssuerId = "testIssuerId";

        tenderService.findAllAndFilterByIssuer(testIssuerId);
        ArgumentCaptor<String> issuerIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(tenderRepository, times(1))
                .findByIssuerId(issuerIdCaptor.capture());
        assertThat(issuerIdCaptor.getValue())
                .isEqualTo(testIssuerId);

        verify(tenderRepository, never()).findAll();
    }

}