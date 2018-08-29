package com.github.markojevtic.restfulapi.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.markojevtic.restfulapi.repository.entity.Offer;
import com.github.markojevtic.restfulapi.resource.dto.OfferDto;
import com.github.markojevtic.restfulapi.service.OfferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OfferResourceUnitTest {

    public static final String TEST_OFFER_ID = "test-offer-id";
    public static final String TEST_TENDER_ID = "test-tender-id";
    public static final String TEST_BIDDER_ID = "test-issuer-id";
    public static final String TEST_DESCRIPTION = "Test description";
    public static final String OVERSIZED_ID = "12356768901235676890123567689012356768901235676890";
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private OfferService offerService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Before
    public void setupMvcMock() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void postCreatesOfferAndReturnItWithStatusCreated() throws Exception {
        OfferDto newOfferDto = newTestOfferDto();

        doReturn(newTestOffer())
                .when(offerService).createOffer(any(Offer.class));

        mvc.perform(post(OfferResource.createLink().toUri())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(newOfferDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.offerId").value(TEST_OFFER_ID))
                .andExpect(jsonPath("$.tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$.bidderId").value(TEST_BIDDER_ID))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(offerService, times(1)).createOffer(any(Offer.class));
    }

    @Test
    public void postReturnsBadRequestStatusWhenServiceThrowsIllegalArgumentException() throws Exception {
        OfferDto newOfferDto = newTestOfferDto();

        doThrow(new IllegalArgumentException())
                .when(offerService).createOffer(any(Offer.class));

        mvc.perform(post(OfferResource.createLink().toUri())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(newOfferDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void postReturnsBadRequestStatusAndMessageWhenInputOfferIsNotValid() throws Exception {
        OfferDto newOfferDto = newTestOfferDto();
        newOfferDto.setBidderId(OVERSIZED_ID);

        doThrow(new UnsupportedOperationException("It should not be thrown!"))
                .when(offerService).createOffer(any(Offer.class));

        mvc.perform(post(OfferResource.createLink().toUri())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(newOfferDto)))
                .andExpect(status().isBadRequest())
                .andExpect((jsonPath("$.message", containsString("bidderId"))))
                .andExpect((jsonPath("$.message", containsString("size must be between 0 and 36"))));
    }


    @Test
    public void getByTenderIdReturnsResultWithStatusOk() throws Exception {
        doReturn(singletonList(newTestOffer()))
                .when(offerService).findByTenderId(anyString());

        mvc.perform(get(OfferResource.createLinkToQueryByTenderId(TEST_TENDER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offerId").value(TEST_OFFER_ID))
                .andExpect(jsonPath("$[0].tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$[0].bidderId").value(TEST_BIDDER_ID))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION))
                .andExpect(jsonPath("$[0].links", hasSize(3)))
                .andExpect(jsonPath("$[0].links[0].rel").value("tenderOffers"))
                .andExpect(jsonPath("$[0].links[0].href").exists())
                .andExpect(jsonPath("$[0].links[1].rel").value("bidderOffers"))
                .andExpect(jsonPath("$[0].links[1].href").exists())
                .andExpect(jsonPath("$[0].links[2].rel").value("tenderAndBidderOffers"))
                .andExpect(jsonPath("$[0].links[2].href").exists());
    }

    @Test
    public void getByBidderIdReturnsResultWithStatusOk() throws Exception {
        doReturn(singletonList(newTestOffer()))
                .when(offerService).findByBidderId(anyString());

        mvc.perform(get(OfferResource.createLinkToQueryByBidderId(TEST_BIDDER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offerId").value(TEST_OFFER_ID))
                .andExpect(jsonPath("$[0].tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$[0].bidderId").value(TEST_BIDDER_ID))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION));
    }

    @Test
    public void getByTenderIdAndBidderIdReturnsResultWithStatusOk() throws Exception {
        doReturn(singletonList(newTestOffer()))
                .when(offerService).findByTenderIdAndBidderId(anyString(), anyString());

        mvc.perform(get(OfferResource.createLinkToQueryByTenderIdAndBidderId(TEST_TENDER_ID, TEST_BIDDER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].offerId").value(TEST_OFFER_ID))
                .andExpect(jsonPath("$[0].tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$[0].bidderId").value(TEST_BIDDER_ID))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION));
    }

    @Test
    public void postAcceptOfferReturnsNoContentIfOfferIsAcceptable() throws Exception {
        doNothing()
                .when(offerService).acceptOffer(anyString());

        mvc.perform(post(OfferResource.createLinkToAcceptOffer(TEST_OFFER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());

        ArgumentCaptor<String> offerIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(offerService, times(1)).acceptOffer(offerIdCaptor.capture());

        assertThat(offerIdCaptor.getValue())
                .isEqualTo(TEST_OFFER_ID);
    }

    @Test
    public void postAcceptOfferReturnsBadRequestWhenGivenIdRefersToNonAcceptableOffer() throws Exception {
        String errorMessage = "Offer is not acceptable";
        doThrow(new IllegalArgumentException(errorMessage))
                .when(offerService).acceptOffer(anyString());

        mvc.perform(post(OfferResource.createLinkToAcceptOffer(TEST_OFFER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMessage));

        ArgumentCaptor<String> offerIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(offerService, times(1)).acceptOffer(offerIdCaptor.capture());

        assertThat(offerIdCaptor.getValue())
                .isEqualTo(TEST_OFFER_ID);
    }

    private OfferDto newTestOfferDto() {
        return OfferDto.builder()
                .offerId(TEST_OFFER_ID)
                .tenderId(TEST_TENDER_ID)
                .bidderId(TEST_BIDDER_ID)
                .description(TEST_DESCRIPTION)
                .build();
    }

    private Offer newTestOffer() {
        return Offer.builder()
                .offerId(TEST_OFFER_ID)
                .tenderId(TEST_TENDER_ID)
                .bidderId(TEST_BIDDER_ID)
                .description(TEST_DESCRIPTION)
                .build();
    }
}