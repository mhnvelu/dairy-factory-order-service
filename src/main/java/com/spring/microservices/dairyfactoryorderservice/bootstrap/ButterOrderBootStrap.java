package com.spring.microservices.dairyfactoryorderservice.bootstrap;

import com.spring.microservices.dairyfactoryorderservice.domain.Customer;
import com.spring.microservices.dairyfactoryorderservice.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ButterOrderBootStrap implements CommandLineRunner {
    public static final String TASTING_ROOM = "Tasting Room";
    public static final String BUTTER_1_UPC = "0631234200036";
    public static final String BUTTER_2_UPC = "0631234300019";
    public static final String BUTTER_3_UPC = "0083783375213";

    public static final UUID BUTTER_1_UUID = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");
    public static final UUID BUTTER_2_UUID = UUID.fromString("a712d914-61ea-4623-8bd0-32c0f6545bfd");
    public static final UUID BUTTER_3_UUID = UUID.fromString("026cc3c8-3a0c-4083-a05b-e908048c1b08");

    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading Customer data...");
        loadCustomerData();
        log.info("Loaded Customer data...");
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerName(TASTING_ROOM)
                    .apiKey(UUID.randomUUID())
                    .build());
        }
    }
}
