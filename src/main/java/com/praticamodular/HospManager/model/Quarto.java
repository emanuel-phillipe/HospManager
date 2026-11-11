package com.praticamodular.HospManager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.enums.SituacaoQuarto;
import com.praticamodular.HospManager.model.enums.TipoQuarto;

@Document("quartos")
public class Quarto {
    @Id
    private String id;
    @Indexed(unique = true)
    private int numero;
    private int andar;
    private int capacidadeMaxima;
    private TipoQuarto tipo;
    private SituacaoQuarto situacao = SituacaoQuarto.DISPONIVEL;

    // Valor calculado pelo service a partir das internações ativas. Não é persistido.
    @Transient
    private int pacientesInternados;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public int getAndar() { return andar; }
    public void setAndar(int andar) { this.andar = andar; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public TipoQuarto getTipo() { return tipo; }
    public void setTipo(TipoQuarto tipo) { this.tipo = tipo; }
    public SituacaoQuarto getSituacao() { return situacao; }
    public void setSituacao(SituacaoQuarto situacao) { this.situacao = situacao; }
    public int getPacientesInternados() { return pacientesInternados; }
    public void setPacientesInternados(int pacientesInternados) {
        if (pacientesInternados < 0) throw new IllegalArgumentException("Quantidade de pacientes não pode ser negativa");
        this.pacientesInternados = pacientesInternados;
        atualizarSituacao();
    }

    public int pacientesInternados() { return pacientesInternados; }

    public boolean possuiVaga() {
        return pacientesInternados < capacidadeMaxima;
    }

    public void atualizarSituacao() {
        if (capacidadeMaxima <= 0) {
            throw new RegraNegocioException("A capacidade máxima do quarto deve ser maior que zero");
        }
        this.situacao = possuiVaga() ? SituacaoQuarto.DISPONIVEL : SituacaoQuarto.OCUPADO;
    }

    public int vagasDisponiveis() {
        return Math.max(0, capacidadeMaxima - pacientesInternados);
    }
}
