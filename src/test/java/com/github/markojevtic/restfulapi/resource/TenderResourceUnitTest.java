package com.github.markojevtic.restfulapi.resource;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.markojevtic.restfulapi.repository.entity.Tender;
import com.github.markojevtic.restfulapi.resource.dto.TenderDto;
import com.github.markojevtic.restfulapi.service.TenderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TenderResourceUnitTest {

    public static final String TEST_TENDER_ID = "test-tender-id";
    public static final String TEST_ISSUER_ID = "test-issuer-id";
    public static final String TEST_DESCRIPTION = "Test description";
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private TenderService tenderService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @Before
    public void setupMvcMock() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void postTenderCreateTenderAndReturnItWithStatusCreated() throws Exception {
        TenderDto newTenderDto = newTestTenderDto();

        doReturn(newTestTender())
                .when(tenderService).createNewTender(any(Tender.class));

        mvc.perform(post(TenderResource.createLink().toUri())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(newTenderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$.issuerId").value(TEST_ISSUER_ID))
                .andExpect(jsonPath("$.description").value(TEST_DESCRIPTION));

        verify(tenderService, times(1)).createNewTender(any(Tender.class));
    }

    @Test
    public void postTenderReturnBadRequestStatusWhenServiceThrowsException() throws Exception {
        TenderDto newTenderDto = newTestTenderDto();

        doThrow(new IllegalArgumentException())
                .when(tenderService).createNewTender(any(Tender.class));

        mvc.perform(post(TenderResource.createLink().toUri())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(newTenderDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getPerformsQueryByIssuerIdAndReturnResultWithStatusOk() throws Exception {
        doReturn(singletonList(newTestTender()))
                .when(tenderService).findAllAndFilterByIssuer(anyString());
        mvc.perform(get(TenderResource.createLinkToQueryByIssuerId(TEST_ISSUER_ID).toString())
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenderId").value(TEST_TENDER_ID))
                .andExpect(jsonPath("$[0].issuerId").value(TEST_ISSUER_ID))
                .andExpect(jsonPath("$[0].description").value(TEST_DESCRIPTION));
    }

    private TenderDto newTestTenderDto() {
        return TenderDto.builder()
                .tenderId(TEST_TENDER_ID)
                .issuerId(TEST_ISSUER_ID)
                .description(TEST_DESCRIPTION)
                .build();
    }

    private Tender newTestTender() {
        return Tender.builder()
                .tenderId(TEST_TENDER_ID)
                .issuerId(TEST_ISSUER_ID)
                .description(TEST_DESCRIPTION)
                .build();
    }
}