package com.praticamodular.HospManager.model;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document("historicos")
public class HistoricoMedico  {
    @Id private String id;
    @Indexed(unique = true) private String pacienteId;
    private List<RegistroAtendimento> registros = new ArrayList<>();


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }
    public List<RegistroAtendimento> getRegistros() { return registros; }
    public void setRegistros(List<RegistroAtendimento> registros) { this.registros = registros; }
}
