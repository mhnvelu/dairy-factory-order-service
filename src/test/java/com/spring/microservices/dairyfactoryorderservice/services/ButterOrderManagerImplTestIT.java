package com.spring.microservices.dairyfactoryorderservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jenspiegsa.wiremockextension.WireMockExtension;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrder;
import com.spring.microservices.dairyfactoryorderservice.domain.ButterOrderLine;
import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.dairyfactoryorderservice.repositories.ButterOrderRepository;
import com.spring.microservices.dairyfactoryorderservice.repositories.CustomerRepository;
import com.spring.microservices.dairyfactoryorderservice.services.butter.ButterServiceRestTemplateImpl;
import com.spring.microservices.model.ButterOrderStatusEnum;
import com.spring.microservices.model.v2.ButterDtoV2;
import com.spring.microservices.model.v2.ButterPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.jenspiegsa.wiremockextension.ManagedWireMockServer.with;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ExtendWith(WireMockExtension.class)
class ButterOrderManagerImplTestIT {

    Customer testCustomer;
    UUID butterId = UUID.randomUUID();
    @Autowired
    private ButterOrderManager butterOrderManager;
    @Autowired
    private ButterOrderRepository butterOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private WireMockServer wireMockServer;

    // from spring context configured already
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.save(Customer.builder().customerName("Test Customer").build());
    }

    @Test
    public void newOrderToAllocate() throws JsonProcessingException, InterruptedException {
        // given
        ButterDtoV2 butterDtoV2 = ButterDtoV2.builder().id(butterId).upc("12345").build();
        ButterPagedList butterPagedList = new ButterPagedList(Arrays.asList(butterDtoV2));
        wireMockServer.stubFor(get(ButterServiceRestTemplateImpl.BUTTER_UPC_PATH_V2 + "12345")
                                       .willReturn(okJson(objectMapper.writeValueAsString(butterDtoV2))));

        ButterOrder butterOrder = createButterOrder();
        ButterOrder savedButterOrder = butterOrderManager.newButterOrder(butterOrder);
        assertNotNull(savedButterOrder);

        await().untilAsserted(() -> {
            ButterOrder foundButterOrder = butterOrderRepository.findById(savedButterOrder.getId()).get();
            assertEquals(ButterOrderStatusEnum.ALLOCATED, foundButterOrder.getOrderStatus());
        });
    }


    @Test
    public void failedValidation() throws JsonProcessingException, InterruptedException {
        // given
        ButterDtoV2 butterDtoV2 = ButterDtoV2.builder().id(butterId).upc("12345").build();
        ButterPagedList butterPagedList = new ButterPagedList(Arrays.asList(butterDtoV2));
        wireMockServer.stubFor(get(ButterServiceRestTemplateImpl.BUTTER_UPC_PATH_V2 + "12345")
                                       .willReturn(okJson(objectMapper.writeValueAsString(butterDtoV2))));

        ButterOrder butterOrder = createButterOrder();
        butterOrder.setCustomerRef("fail-validation");
        ButterOrder savedButterOrder = butterOrderManager.newButterOrder(butterOrder);
        assertNotNull(savedButterOrder);

        await().untilAsserted(() -> {
            ButterOrder foundButterOrder = butterOrderRepository.findById(savedButterOrder.getId()).get();
            assertEquals(ButterOrderStatusEnum.VALIDATION_EXCEPTION, foundButterOrder.getOrderStatus());
        });

    }


    @Test
    public void newOrderToPickup() throws JsonProcessingException, InterruptedException {
        // given
        ButterDtoV2 butterDtoV2 = ButterDtoV2.builder().id(butterId).upc("12345").build();
        ButterPagedList butterPagedList = new ButterPagedList(Arrays.asList(butterDtoV2));
        wireMockServer.stubFor(get(ButterServiceRestTemplateImpl.BUTTER_UPC_PATH_V2 + "12345")
                                       .willReturn(okJson(objectMapper.writeValueAsString(butterDtoV2))));

        ButterOrder butterOrder = createButterOrder();
        ButterOrder savedButterOrder = butterOrderManager.newButterOrder(butterOrder);
        assertNotNull(savedButterOrder);

        await().untilAsserted(() -> {
            ButterOrder foundButterOrder = butterOrderRepository.findById(savedButterOrder.getId()).get();
            assertEquals(ButterOrderStatusEnum.ALLOCATED, foundButterOrder.getOrderStatus());
        });

        butterOrderManager.butterOrderPickedUp(savedButterOrder.getId());

        await().untilAsserted(() -> {
            ButterOrder foundButterOrder = butterOrderRepository.findById(savedButterOrder.getId()).get();
            assertEquals(ButterOrderStatusEnum.PICKED_UP, foundButterOrder.getOrderStatus());
        });
    }

    private ButterOrder createButterOrder() {
        ButterOrder butterOrder = ButterOrder.builder().customer(testCustomer).build();
        Set<ButterOrderLine> lines = new HashSet<>();
        lines.add(ButterOrderLine.builder().butterId(butterId).upc("12345").orderQuantity(1).butterOrder(butterOrder).build());
        butterOrder.setButterOrderLines(lines);
        return butterOrder;
    }

    @TestConfiguration
    static class RestTemplateBuilderProvider {
        @Bean(destroyMethod = "stop")
        public WireMockServer wireMockServer() {
            WireMockServer wireMockServer = with(wireMockConfig().port(8083));
            wireMockServer.start();
            return wireMockServer;
        }
    }

}