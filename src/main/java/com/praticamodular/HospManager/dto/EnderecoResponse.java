package com.praticamodular.HospManager.dto;

public record EnderecoResponse(String logradouro, String numero, String complemento,
        String bairro, String cidade, String estado, String cep) {}
