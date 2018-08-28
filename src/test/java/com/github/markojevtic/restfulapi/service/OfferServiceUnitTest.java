package com.github.markojevtic.restfulapi.service;

import com.github.markojevtic.restfulapi.repository.OfferRepository;
import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.repository.entity.OfferStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OfferServiceUnitTest {

    public static final String TEST_TENDER_ID = "testTenderId";
    public static final String TEST_BIDDER_ID = "testBidderId";
    public static final String TEST_DESCRIPTION = "This is a great offer!";
    public static final boolean IT_IS_BIDDABLE = true;
    public static final boolean IT_IS_NO_BIDDABLE = false;


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
}