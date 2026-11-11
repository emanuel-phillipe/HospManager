package com.praticamodular.HospManager.model;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.StatusInternacao;

@Document("internacoes")
public class Internacao {
    @Id
    private String id;
    private String pacienteId;
    private String profissionalId;
    private String quartoId;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaAlta;
    private LocalDate dataEfetivaAlta;
    private String observacoes;
    private StatusInternacao status = StatusInternacao.ATIVA;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }
    public String getProfissionalId() { return profissionalId; }
    public void setProfissionalId(String profissionalId) { this.profissionalId = profissionalId; }
    public String getQuartoId() { return quartoId; }
    public void setQuartoId(String quartoId) { this.quartoId = quartoId; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }
    public LocalDate getDataPrevistaAlta() { return dataPrevistaAlta; }
    public void setDataPrevistaAlta(LocalDate dataPrevistaAlta) { this.dataPrevistaAlta = dataPrevistaAlta; }
    public LocalDate getDataEfetivaAlta() { return dataEfetivaAlta; }
    public void setDataEfetivaAlta(LocalDate dataEfetivaAlta) { this.dataEfetivaAlta = dataEfetivaAlta; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public StatusInternacao getStatus() { return status; }
    public void setStatus(StatusInternacao status) { this.status = status; }

    public void registrarAlta(LocalDate data) {
        if (!estaAtiva()) {
            throw new RegraNegocioException("Esta internação já foi encerrada");
        }
        if (data == null) {
            throw new RegraNegocioException("A data efetiva da alta é obrigatória");
        }
        if (dataEntrada != null && data.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A data da alta não pode ser anterior à entrada");
        }
        this.dataEfetivaAlta = data;
        this.status = StatusInternacao.ENCERRADA;
    }

    public boolean estaAtiva() {
        return this.status == StatusInternacao.ATIVA;
    }
}
