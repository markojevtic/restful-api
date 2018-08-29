package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.OfferRepository;
import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OfferServiceUnitTest {

    public static final String TEST_TENDER_ID = "testTenderId";
    public static final String TEST_BIDDER_ID = "testBidderId";
    public static final String TEST_DESCRIPTION = "This is a great offer!";
    public static final boolean IT_IS_BIDDABLE = true;
    public static final boolean IT_IS_NO_BIDDABLE = false;
    public static final String TEST_OFFER_ID = "testOfferId";


    @MockBean
    private TenderService tenderService;

    @MockBean
    private OfferRepository offerRepository;

    @Autowired
    private OfferService offerService;

    @Test
    public void createOfferValidatesAndStoresOffer() {
        Offer validOffer = Offer.builder()
                .tenderId(TEST_TENDER_ID)
                .bidderId(TEST_BIDDER_ID)
                .description(TEST_DESCRIPTION)
                .build();

        doReturn(IT_IS_BIDDABLE)
                .when(tenderService).isTenderBiddable(anyString());

        doAnswer(invocation -> invocation.getArguments()[0])
                .when(offerRepository).save(any(Offer.class));

        Offer createdOffer = offerService.createOffer(validOffer);

        assertThat(createdOffer)
                .isEqualToComparingFieldByField(validOffer);
        assertThat(createdOffer.getOfferId())
                .isNotEmpty();
        assertThat(createdOffer.getStatus())
                .isEqualTo(OfferStatus.NEW);
    }

    @Test
    public void createOfferTrowsExceptionWhenThereIsNoBiddableTender() {
        Offer validOffer = Offer.builder()
                .tenderId(TEST_TENDER_ID)
                .bidderId(TEST_BIDDER_ID)
                .description(TEST_DESCRIPTION)
                .build();

        doReturn(IT_IS_NO_BIDDABLE)
                .when(tenderService).isTenderBiddable(anyString());

        assertThatThrownBy(() -> offerService.createOffer(validOffer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("There is no tender open for bidding!");
    }

    @Test
    public void createOfferTrowsExceptionWhenThereIsNoBidderId() {
        Offer invalidOffer = Offer.builder()
                .tenderId(TEST_TENDER_ID)
                .description(TEST_DESCRIPTION)
                .build();

        doReturn(IT_IS_BIDDABLE)
                .when(tenderService).isTenderBiddable(anyString());

        assertThatThrownBy(() -> offerService.createOffer(invalidOffer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offer must have a valid bidder id!");
    }

    @Test
    public void acceptOfferAcceptTargetOfferAndDeclineAllOtherAndCloseTender() {
        Offer targetOffer = existingOffer(TEST_OFFER_ID);
        Offer anotherOffer = existingOffer("anotherId");

        doReturn(Optional.of(targetOffer))
                .when(offerRepository).findById(anyString());
        doReturn(singletonList(anotherOffer))
                .when(offerRepository).findByTenderId(anyString());

        offerService.acceptOffer(TEST_OFFER_ID);

        ArgumentCaptor<Offer> acceptedCaptor = ArgumentCaptor.forClass(Offer.class);
        verify(offerRepository, times(1)).save(acceptedCaptor.capture());
        assertThat(acceptedCaptor.getValue().getOfferId())
                .isEqualTo(TEST_OFFER_ID);
        assertThat(acceptedCaptor.getValue().getStatus())
                .isEqualTo(OfferStatus.ACCEPTED);

        ArgumentCaptor<Iterable<Offer>> declinedOffersCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(offerRepository, times(1)).saveAll(declinedOffersCaptor.capture());
        assertThat(declinedOffersCaptor.getValue())
                .extracting(Offer::getStatus)
                .allMatch(offerStatus -> offerStatus == OfferStatus.DECLINED);

        ArgumentCaptor<String> tenderIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(tenderService, times(1)).closeTender(tenderIdCaptor.capture());
        assertThat(tenderIdCaptor.getValue())
                .isEqualTo(TEST_TENDER_ID);
    }

    @Test
    public void acceptOfferThrowsExceptionIfOfferDoesNotHaveStatusNew() {
        Offer targetOffer = existingOffer(TEST_OFFER_ID);
        targetOffer.setStatus(OfferStatus.ACCEPTED);
        doReturn(Optional.of(targetOffer))
                .when(offerRepository).findById(anyString());
        assertThatThrownBy( () -> offerService.acceptOffer(TEST_OFFER_ID))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Target offer must have status NEW!");

        verify(offerRepository, never()).save(any(Offer.class));
        verify(offerRepository, never()).saveAll(anyList());
        verify(tenderService, never()).closeTender(anyString());
    }

    @Test
    public void acceptOfferThrowsExceptionIfOfferDoesNotExist() {
        Offer targetOffer = existingOffer(TEST_OFFER_ID);
        targetOffer.setStatus(OfferStatus.ACCEPTED);
        doReturn(Optional.empty())
                .when(offerRepository).findById(anyString());
        assertThatThrownBy( () -> offerService.acceptOffer(TEST_OFFER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("OfferId refers to non existing offer!");

        verify(offerRepository, never()).save(any(Offer.class));
        verify(offerRepository, never()).saveAll(anyList());
        verify(tenderService, never()).closeTender(anyString());
    }



    private Offer existingOffer(String offerId) {
        return Offer.builder()
                .offerId(offerId)
                .tenderId(TEST_TENDER_ID)
                .description(offerId+ ":" +TEST_DESCRIPTION)
                .status(OfferStatus.NEW)
                .build();
    }


}