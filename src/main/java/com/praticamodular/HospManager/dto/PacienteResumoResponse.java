package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
public record PacienteResumoResponse(String id, String nome, String cpf, LocalDate dataNascimento, boolean ativo) {}
