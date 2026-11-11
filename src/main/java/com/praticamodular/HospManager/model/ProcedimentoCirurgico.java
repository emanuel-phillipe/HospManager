package com.praticamodular.HospManager.model;

import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.exception.TransicaoInvalidaException;
import com.praticamodular.HospManager.model.enums.TipoCirurgia;
import com.praticamodular.HospManager.model.enums.StatusProcedimento;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Procedimento cirúrgico. Data/horário herdados de Atendimento são o AGENDAMENTO;
 * dataEfetiva/horarioEfetivo são preenchidos quando a cirurgia realmente começa.
 *
 * Ordem de uso esperada pelo serviço: validarCondicoes() -> cirurgiao.registrarProcedimento(this).
 * (validarCondicoes checa conflito de agenda, então deve rodar antes de registrar o procedimento no cirurgião.)
 */
public class ProcedimentoCirurgico extends Atendimento {

    private final Cirurgiao cirurgiao;
    private final String descricao;
    private final TipoCirurgia tipoCirurgia;
    private String observacoesPreOperatorias;
    private String observacoesPosOperatorias;
    private LocalDate dataEfetiva;
    private LocalTime horarioEfetivo;
    private StatusProcedimento status = StatusProcedimento.AGENDADO;

    public ProcedimentoCirurgico(String id, Paciente paciente, LocalDate data, LocalTime horario,
                                 String observacoes, Cirurgiao cirurgiao, String descricao,
                                 TipoCirurgia tipoCirurgia, String observacoesPreOperatorias) {
        this.id = id;
        this.pacienteId = paciente == null ? null : paciente.getId();
        this.data = data;
        this.horario = horario;
        this.observacoes = observacoes;
        this.cirurgiao = cirurgiao;
        this.descricao = descricao;
        this.tipoCirurgia = tipoCirurgia;
        this.observacoesPreOperatorias = observacoesPreOperatorias;
        validarDadosObrigatorios();
    }

    // ---------- transições ----------

    public void iniciar() {
        validarTransicao(StatusProcedimento.EM_ANDAMENTO);
        validarDadosObrigatorios();
        if (tipoCirurgia.exigePreOperatorio() && estaVazio(observacoesPreOperatorias)) {
            throw new RegraNegocioException(
                    "Cirurgia " + tipoCirurgia + " exige observações pré-operatórias para iniciar");
        }
        this.status = StatusProcedimento.EM_ANDAMENTO;
        this.dataEfetiva = LocalDate.now();
        this.horarioEfetivo = LocalTime.now();
    }

    /** Só conclui se o procedimento foi iniciado (EM_ANDAMENTO); a transição é validada primeiro. */
    public void concluir() {
        validarTransicao(StatusProcedimento.CONCLUIDO);
        if (estaVazio(observacoesPosOperatorias)) {
            throw new RegraNegocioException("Registre as observações pós-operatórias antes de concluir");
        }
        this.status = StatusProcedimento.CONCLUIDO;
    }

    public void cancelar() {
        validarTransicao(StatusProcedimento.CANCELADO);
        this.status = StatusProcedimento.CANCELADO;
    }

    // ---------- observações (só no momento certo) ----------

    public void registrarObservacoesPreOperatorias(String obs) {
        if (status != StatusProcedimento.AGENDADO) {
            throw new RegraNegocioException("Observações pré-operatórias só podem ser alteradas enquanto AGENDADO");
        }
        this.observacoesPreOperatorias = obs;
    }

    public void registrarObservacoesPosOperatorias(String obs) {
        if (status != StatusProcedimento.EM_ANDAMENTO) {
            throw new RegraNegocioException("Observações pós-operatórias só podem ser registradas EM_ANDAMENTO");
        }
        this.observacoesPosOperatorias = obs;
    }

    // ---------- validações ----------

    /**
     * Dados mínimos + agenda do cirurgião.
     * - Conflito de horário vale para TODOS os tipos (regra 3: nenhum profissional em dois atendimentos no mesmo horário).
     * - Disponibilidade de expediente é ignorada só na EMERGENCIAL (cirurgia imediata, fora da grade de horários).
     */
    @Override
    public void validarCondicoes() {
        validarDadosObrigatorios();
        if (tipoCirurgia != TipoCirurgia.EMERGENCIAL && !cirurgiao.estaDisponivel(data, horario)) {
            throw new RegraNegocioException("Cirurgião indisponível em " + data + " " + horario);
        }
        if (cirurgiao.possuiConflito(data, horario)) {
            throw new RegraNegocioException("Cirurgião já possui compromisso em " + data + " " + horario);
        }
    }

    private void validarDadosObrigatorios() {
        if (pacienteId == null || pacienteId.isBlank()) throw new RegraNegocioException("Paciente é obrigatório");
        if (cirurgiao == null) throw new RegraNegocioException("Cirurgião é obrigatório");
        if (tipoCirurgia == null) throw new RegraNegocioException("Tipo de cirurgia é obrigatório");
        if (estaVazio(descricao)) throw new RegraNegocioException("Descrição é obrigatória");
        if (data == null || horario == null) throw new RegraNegocioException("Data/horário são obrigatórios");
        if (!tipoCirurgia.permiteAgendamentoFuturo() && LocalDateTime.of(data, horario).isAfter(LocalDateTime.now())) {
            throw new RegraNegocioException("Cirurgia " + tipoCirurgia + " não pode ser agendada para o futuro");
        }
    }

    private void validarTransicao(StatusProcedimento novo) {
        if (!status.podeTransicionarPara(novo)) {
            throw new TransicaoInvalidaException("Transição inválida de procedimento: " + status + " -> " + novo);
        }
    }

    private static boolean estaVazio(String s) {
        return s == null || s.isBlank();
    }

    @Override
    public ProfissionalSaude getResponsavel() {
        return cirurgiao;
    }

    public Cirurgiao getCirurgiao() { return cirurgiao; }
    public String getDescricao() { return descricao; }
    public TipoCirurgia getTipoCirurgia() { return tipoCirurgia; }
    public StatusProcedimento getStatus() { return status; }
    public String getObservacoesPreOperatorias() { return observacoesPreOperatorias; }
    public String getObservacoesPosOperatorias() { return observacoesPosOperatorias; }
    public LocalDate getDataEfetiva() { return dataEfetiva; }
    public LocalTime getHorarioEfetivo() { return horarioEfetivo; }
}
