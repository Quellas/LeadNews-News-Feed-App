package com.itheima.media.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OuterConfig {

    OuterConfig() {
        System.out.println("OuterConfig init...");
    }

    @Bean
    static Parent parent() {
        return new Parent();
    }

    @Configuration
    private static class InnerConfig {
        InnerConfig() {
            System.out.println("InnerConfig init...");
        }

        @Bean
        Daughter daughter() {
            return new Daughter();
        }
    }
}