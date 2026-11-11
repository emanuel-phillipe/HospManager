
package com.praticamodular.HospManager.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import com.praticamodular.HospManager.model.enums.Especialidade;

public record ProfissionalRequest(

    @NotBlank(message = "Nome obrigatório")
    String nome,

    @NotBlank(message = "Registro profissional obrigatório")
    String registroProfissional,

    @NotBlank(message = "Telefone obrigatório")
    @Pattern(
        regexp = "(?=(?:[^0-9]*[0-9]){10,13}[^0-9]*$)[+()0-9 .-]+",
        message = "Telefone inválido: informe de 10 a 13 dígitos"
    )
    String telefone,

    @NotBlank(message = "E-mail obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Tipo de profissional obrigatório")
    String tipo,

    @NotNull(message = "Especialidade obrigatória")
    Especialidade especialidade,

    @Valid
    List<DisponibilidadeRequest> disponibilidades

) {}
