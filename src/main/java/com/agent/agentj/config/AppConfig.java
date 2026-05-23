package com.agent.agentj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    /**
     * RestTemplate is the Spring way to make HTTP calls.
     * We declare it as a @Bean so Spring can inject it anywhere it's needed.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
