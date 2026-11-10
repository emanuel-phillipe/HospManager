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
