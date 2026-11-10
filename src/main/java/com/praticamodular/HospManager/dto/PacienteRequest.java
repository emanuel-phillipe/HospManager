package com.praticamodular.HospManager.dto;

import java.time.LocalDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import com.praticamodular.HospManager.validation.Cpf;

public record PacienteRequest(
        @NotBlank(message = "Nome obrigatório") String nome,
        @NotBlank(message = "CPF obrigatório") @Cpf String cpf,
        @NotNull(message = "Data de nascimento obrigatória") @PastOrPresent(message = "Data de nascimento não pode ser futura") LocalDate dataNascimento,
        @NotBlank(message = "Telefone obrigatório")
        @Pattern(regexp = "(?=(?:[^0-9]*[0-9]){10,13}[^0-9]*$)[+()0-9 .-]+", message = "Telefone inválido: informe de 10 a 13 dígitos") String telefone,
        @NotBlank(message = "E-mail obrigatório") @Email(message = "E-mail inválido")
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "E-mail inválido") String email,
        @NotNull(message = "Endereço obrigatório") @Valid EnderecoRequest endereco) {}
