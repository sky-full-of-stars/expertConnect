package com.uci.expertConnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expertConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ExpertConnect API")
                        .description("API for connecting experts with clients for one-on-one meetings")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("ExpertConnect Team")
                                .email("expertconnectforyou@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
} 