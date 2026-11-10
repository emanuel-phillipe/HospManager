package com.praticamodular.HospManager.model;

import java.time.LocalDateTime;
import com.praticamodular.HospManager.model.enums.TipoRegistro;


public class RegistroAtendimento  {
    private String id;
    private LocalDateTime dataHora;
    private TipoRegistro tipo;
    private String descricao;
    private String atendimentoId;
    private String internacaoId;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoRegistro getTipo() { return tipo; }
    public void setTipo(TipoRegistro tipo) { this.tipo = tipo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(String atendimentoId) { this.atendimentoId = atendimentoId; }
    public String getInternacaoId() { return internacaoId; }
    public void setInternacaoId(String internacaoId) { this.internacaoId = internacaoId; }
}
