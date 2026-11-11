package com.praticamodular.HospManager.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

// Base de autenticaçao
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain seguranca(HttpSecurity http,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver erros) throws Exception {
        return http
                .httpBasic(org.springframework.security.config.Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/pacientes", "/pacientes/**",
                        "/profissionais", "/profissionais/**", "/consultas", "/consultas/**",
                        "/internacoes", "/internacoes/**", "/quartos", "/quartos/**",
                        "/procedimentos", "/procedimentos/**", "/emergencias", "/emergencias/**"))
                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint((request, response, erro) ->
                                erros.resolveException(request, response, null, erro))
                        .accessDeniedHandler((request, response, erro) ->
                                erros.resolveException(request, response, null, erro)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/pacientes", "/pacientes/**", "/docs", "/swagger-ui/**",
                                "/v3/api-docs", "/v3/api-docs/**", "/error").permitAll()
                        .anyRequest().authenticated())
                .build();
    }
}
