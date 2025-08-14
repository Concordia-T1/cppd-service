package com.java.petrovsm.concordiacppdservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {

    @Value("${cppd.token.expiration-days:7}")
    private int tokenExpirationDays;

}
