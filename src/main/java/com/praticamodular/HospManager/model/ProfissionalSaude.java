package com.praticamodular.HospManager.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.praticamodular.HospManager.model.enums.Especialidade;

@Document("profissionais")
public abstract class ProfissionalSaude extends Pessoa {

    @Indexed(unique = true)
    protected String registroProfissional;
    private Especialidade especialidade;
    protected List<Disponibilidade> disponibilidades = new ArrayList<>();
    private boolean ativo = true;

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    public List<Disponibilidade> getDisponibilidades() {
        return disponibilidades;
    }

    public void setDisponibilidades(List<Disponibilidade> disponibilidades) {
        this.disponibilidades = disponibilidades;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean estaDisponivel(Disponibilidade disponibilidade) {
        if (disponibilidades == null) {
            return false;
        }

        return disponibilidades.contains(disponibilidade);
    }

    public boolean possuiConflito() {
        if (disponibilidades == null) {
            return false;
        }

        for (int i = 0; i < disponibilidades.size(); i++) {
            for (int j = i + 1; j < disponibilidades.size(); j++) {

                if (disponibilidades.get(i)
                        .conflitaCom(disponibilidades.get(j))) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getAtuacao() {
        return especialidade == null ? "" : especialidade.name();
    }

    public boolean estaDisponivel(LocalDate data, LocalTime horario) {
        return disponibilidades != null && disponibilidades.stream().anyMatch(d -> d.cobre(data, horario));
    }

    @Transient
    private final List<Atendimento> atendimentos = new ArrayList<>();

    public void registrarAtendimento(Atendimento atendimento) {
        if (atendimento == null) {
            throw new IllegalArgumentException("Atendimento é obrigatório");
        }
        if (!atendimentos.contains(atendimento)) atendimentos.add(atendimento);
    }

    public boolean possuiConflito(LocalDate data, LocalTime horario) {
        return atendimentos.stream().anyMatch(a -> {
            boolean ativo = switch (a) {
                case Consulta c -> !c.getStatus().isFinal();
                case ProcedimentoCirurgico p -> !p.getStatus().isFinal();
                case AtendimentoEmergencia e -> !e.getStatus().isFinal();
                default -> true;
            };
            return ativo && Objects.equals(a.getData(), data)
                    && Objects.equals(a.getHorario(), horario);
        });
    }
}
