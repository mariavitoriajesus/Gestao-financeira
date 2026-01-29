package com.beca.financial.transaction_processor.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpConfig {

    @Bean
    RestClient transactionApiClient(@Value("${transaction.api.base-url}") String baseUrl){
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
