package com.praticamodular.HospManager.model;

import java.time.LocalDate;
import java.time.Period;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document("pacientes")
public class Paciente extends Pessoa {
    @Indexed(unique = true) private String cpf;
    @PastOrPresent private LocalDate dataNascimento;
    private Endereco endereco;
    private boolean ativo = true;

    @org.springframework.data.annotation.Transient
    private HistoricoMedico historico;

    public HistoricoMedico obterHistorico() {
        if (historico == null) throw new IllegalStateException("Histórico deve ser carregado pelo serviço");
        return historico;
    }

    public void definirHistorico(HistoricoMedico historico) {
        if (historico == null || !java.util.Objects.equals(getId(), historico.getPacienteId())) {
            throw new IllegalArgumentException("Histórico não pertence ao paciente");
        }
        this.historico = historico;
    }

    public int calcularIdade() {
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
