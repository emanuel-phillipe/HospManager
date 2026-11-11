
package com.praticamodular.HospManager.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Disponibilidade {
    private int id;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFim;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public boolean cobre(LocalDate data, LocalTime horario) {
        return data != null && horario != null && horaInicio != null && horaFim != null
                && data.getDayOfWeek() == diaSemana
                && !horario.isBefore(horaInicio)
                && horario.isBefore(horaFim);
    }


public boolean conflitaCom(Disponibilidade outra) {

    // Se forem dias diferentes, não existe conflito
    if (this.diaSemana != outra.diaSemana) {
        return false;
    }

    // Verifica se os horários se sobrepõem
    return this.horaInicio.isBefore(outra.horaFim)
            && outra.horaInicio.isBefore(this.horaFim);
}

}
