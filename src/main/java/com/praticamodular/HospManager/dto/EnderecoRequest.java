package com.praticamodular.HospManager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EnderecoRequest(
        @NotBlank(message = "Logradouro obrigatório") String logradouro,
        @NotBlank(message = "Número obrigatório") String numero,
        String complemento,
        @NotBlank(message = "Bairro obrigatório") String bairro,
        @NotBlank(message = "Cidade obrigatória") String cidade,
        @NotBlank(message = "Estado obrigatório") @Pattern(regexp = "[A-Za-z]{2}", message = "Estado deve ter duas letras") String estado,
        @NotBlank(message = "CEP obrigatório") @Pattern(regexp = "[0-9]{8}", message = "CEP deve ter oito dígitos") String cep) {}
