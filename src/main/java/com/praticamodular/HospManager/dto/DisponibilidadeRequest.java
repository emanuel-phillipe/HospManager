
package com.praticamodular.HospManager.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadeRequest(

    @NotNull(message = "Dia da semana obrigatório")
    DayOfWeek diaSemana,

    @NotNull(message = "Horário inicial obrigatório")
    LocalTime horaInicio,

    @NotNull(message = "Horário final obrigatório")
    LocalTime horaFim

) {}
