package ru.concordia.cppd_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.concordia.cppd_service.service.props.EcdhLinkProperties;

@SpringBootApplication
@EnableConfigurationProperties({EcdhLinkProperties.class})
public class CppdServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CppdServiceApplication.class, args);
    }
}
