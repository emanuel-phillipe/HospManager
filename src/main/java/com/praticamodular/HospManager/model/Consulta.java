package com.praticamodular.HospManager.model;
import org.springframework.data.mongodb.core.mapping.Document;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.StatusConsulta;
import com.praticamodular.HospManager.model.enums.TipoConsulta;

@Document("consultas")
public abstract class Consulta extends Atendimento {
    protected String motivo;
    protected StatusConsulta status = StatusConsulta.AGENDADA;

    public abstract TipoConsulta getTipo();

    public abstract void definirMedico(ProfissionalSaude profissional);

    protected abstract void validarModalidade();

    public void confirmar() { trocarStatus(StatusConsulta.CONFIRMADA); }

    public void iniciar() {
        ProfissionalSaude medico = getResponsavel();
        if (medico == null || !medico.isAtivo()) throw new RegraNegocioException("Médico responsável deve estar ativo");
        if (medico.possuiAtendimentoEmAndamento(id)) throw new RegraNegocioException("Médico já está atendendo outro paciente");
        trocarStatus(StatusConsulta.EM_ATENDIMENTO);
    }

    public void concluir(String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            throw new RegraNegocioException("Observações são obrigatórias para concluir a consulta");
        }
        trocarStatus(StatusConsulta.CONCLUIDA);
        this.observacoes = observacoes.trim();
    }

    public void cancelar() { trocarStatus(StatusConsulta.CANCELADA); }

    public void registrarNaoComparecimento() { trocarStatus(StatusConsulta.NAO_COMPARECEU); }

    private void trocarStatus(StatusConsulta novo) {
        if (!status.podeTransicionarPara(novo)) {
            throw new RegraNegocioException(
                    "Não é possível mudar a consulta de " + status + " para " + novo);
        }
        this.status = novo;
    }

    @Override
    public void validarCondicoes() {
        if (pacienteId == null || pacienteId.isBlank()) {
            throw new RegraNegocioException("Paciente é obrigatório");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException("Motivo da consulta é obrigatório");
        }
        ProfissionalSaude medico = getResponsavel();
        if (medico == null) {
            throw new RegraNegocioException("Médico responsável é obrigatório");
        }
        if (!medico.isAtivo()) {
            throw new RegraNegocioException("Médico inativo não pode receber consultas");
        }
        validarModalidade();
        exigirDataHoraFutura();
        if (medico.possuiConflito(data, horario, id)) {
            throw new RegraNegocioException("Médico já possui atendimento neste horário");
        }
        if (!medicoDisponivel(medico)) {
            throw new RegraNegocioException("Médico não está disponível nesse dia e horário");
        }
    }

    private boolean medicoDisponivel(ProfissionalSaude medico) {
        if (medico.getDisponibilidades() == null) {
            return false;
        }
        return medico.getDisponibilidades().stream().anyMatch(d ->
                d.getDiaSemana() == data.getDayOfWeek()
                && d.getHoraInicio() != null && d.getHoraFim() != null
                && !horario.isBefore(d.getHoraInicio())
                && horario.isBefore(d.getHoraFim()));
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public StatusConsulta getStatus() { return status; }
}
