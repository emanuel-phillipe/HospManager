package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
public record PacienteResponse(String id, String nome, String cpf, LocalDate dataNascimento,
        String telefone, String email, EnderecoResponse endereco, boolean ativo, int idade) {}
