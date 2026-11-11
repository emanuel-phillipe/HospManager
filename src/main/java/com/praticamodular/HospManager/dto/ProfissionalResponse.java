
package com.praticamodular.HospManager.dto;

import java.util.List;

import com.praticamodular.HospManager.model.enums.Especialidade;

public record ProfissionalResponse(

    String id,
    String nome,
    String registroProfissional,
    String telefone,
    String email,
    String tipo,
    Especialidade especialidade,
    List<DisponibilidadeRequest> disponibilidades,
    boolean ativo

) {}
