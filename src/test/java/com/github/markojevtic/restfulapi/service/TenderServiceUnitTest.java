package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.TenderRepository;
import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.repository.entity.TenderStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TenderServiceUnitTest {
    public static final String TEST_TENDER_ID = "testTenderId";
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

        Tender createdTender = tenderService.createNewTender(testTender);

        assertThat(createdTender.getTenderId())
                .isNotEmpty();

        assertThat(createdTender.getStatus())
                .isEqualTo(TenderStatus.OPEN);

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

    @Test
    public void isBiddableReturnsTrueIfTenderStatusIsOpen() {

        Optional<Tender> existingTender = Optional.of(Tender.builder()
                .tenderId(TEST_TENDER_ID)
                .status(TenderStatus.OPEN)
                .build());

        doReturn(existingTender)
                .when(tenderRepository).findById(anyString());

        assertThat(tenderService.isTenderBiddable(TEST_TENDER_ID))
                .isTrue();
    }

    @Test
    public void isBiddableReturnsFalseWhenTenderDoesNotExistOrStatusIsClose() {


        Optional<Tender> existingTender = Optional.of(Tender.builder()
                .tenderId(TEST_TENDER_ID)
                .status(TenderStatus.CLOSE)
                .build());
        Optional<Tender> noTender = Optional.empty();

        doReturn(existingTender, noTender)
                .when(tenderRepository).findById(anyString());

        assertThat(tenderService.isTenderBiddable(TEST_TENDER_ID))
                .isFalse();

        assertThat(tenderService.isTenderBiddable(TEST_TENDER_ID))
                .isFalse();
    }

    @Test
    public void closeOfferDoesCloseForOpenTender() {
        Optional<Tender> existingTender = Optional.of(Tender.builder()
                .tenderId(TEST_TENDER_ID)
                .status(TenderStatus.OPEN)
                .build());

        doReturn(existingTender)
                .when(tenderRepository).findById(anyString());

        tenderService.closeTender(TEST_TENDER_ID);

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(tenderRepository, times(1)).findById(idCaptor.capture());
        assertThat(idCaptor.getValue())
                .isEqualTo(TEST_TENDER_ID);

        ArgumentCaptor<Tender> tenderCaptor = ArgumentCaptor.forClass(Tender.class);
        verify(tenderRepository, times(1)).save(tenderCaptor.capture());
        assertThat(tenderCaptor.getValue())
                .isEqualTo(existingTender.get());

        assertThat(tenderCaptor.getValue().getStatus())
                .isEqualTo(TenderStatus.CLOSE);
    }

    @Test
    public void closeOfferThrowsExceptionIfTenderHasStatusClose() {
        Optional<Tender> existingTender = Optional.of(Tender.builder()
                .tenderId(TEST_TENDER_ID)
                .status(TenderStatus.CLOSE)
                .build());

        doReturn(existingTender)
                .when(tenderRepository).findById(anyString());

        assertThatThrownBy( () -> tenderService.closeTender(TEST_TENDER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("It's only possible to close an open Tender.");
    }

    @Test
    public void closeOfferThrowsExceptionIfTenderDoesNotExists() {
        Optional<Tender> thereIsNoTender = Optional.empty();

        doReturn(thereIsNoTender)
                .when(tenderRepository).findById(anyString());

        assertThatThrownBy( () -> tenderService.closeTender(TEST_TENDER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Is not possible to close a non existing Tender.");
    }

}