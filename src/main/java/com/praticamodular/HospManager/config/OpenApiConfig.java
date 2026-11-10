package com.praticamodular.HospManager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//Config básica do swagger
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI hospitalOpenApi() {
        return new OpenAPI().info(new Info().title("HospManager API").version("1.0.0")
                .description("API do HospManager: pacientes, histórico médico e integração entre módulos."));
    }
}
