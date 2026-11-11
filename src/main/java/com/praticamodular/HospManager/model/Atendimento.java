package com.praticamodular.HospManager.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.data.annotation.Id;
import com.praticamodular.HospManager.exception.RegraNegocioException;

public abstract class Atendimento {
    @Id protected String id;
    protected String pacienteId;
    protected LocalDate data;
    protected LocalTime horario;
    protected String observacoes;

    public abstract ProfissionalSaude getResponsavel();

    public abstract void validarCondicoes();

    public String getResponsavelId() {
        ProfissionalSaude responsavel = getResponsavel();
        return responsavel == null ? null : responsavel.getId();
    }

    public boolean estaEmAndamento() {
        return switch (this) {
            case Consulta c -> c.getStatus() == com.praticamodular.HospManager.model.enums.StatusConsulta.EM_ATENDIMENTO;
            case ProcedimentoCirurgico p -> p.getStatus() == com.praticamodular.HospManager.model.enums.StatusProcedimento.EM_ANDAMENTO;
            case AtendimentoEmergencia e -> e.getStatus() == com.praticamodular.HospManager.model.enums.StatusEmergencia.EM_ATENDIMENTO;
            default -> false;
        };
    }

    protected void exigirDataHoraFutura() {
        if (data == null || horario == null) {
            throw new RegraNegocioException("Data e horário são obrigatórios");
        }
        if (!LocalDateTime.of(data, horario).isAfter(LocalDateTime.now())) {
            throw new RegraNegocioException("Data e horário do atendimento devem estar no futuro");
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
