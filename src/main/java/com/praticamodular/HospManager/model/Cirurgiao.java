
package com.praticamodular.HospManager.model;

import java.util.ArrayList;
import java.util.List;

import com.praticamodular.HospManager.model.enums.Especialidade;

public class Cirurgiao extends ProfissionalSaude {

    private List<String> procedimentosIds = new ArrayList<>();

    public Cirurgiao() {
        setEspecialidade(Especialidade.CIRURGIA_GERAL);
    }

    public List<String> getProcedimentosIds() {
        return procedimentosIds;
    }

    public void setProcedimentosIds(List<String> procedimentosIds) {
        this.procedimentosIds = procedimentosIds;
    }

    public void registrarProcedimento(String procedimentoId) {
        if (procedimentoId == null || procedimentoId.isBlank()) {
            throw new IllegalArgumentException(
                "O ID do procedimento é obrigatório"
            );
        }

        if (procedimentosIds == null) {
            procedimentosIds = new ArrayList<>();
        }

        if (!procedimentosIds.contains(procedimentoId)) {
            procedimentosIds.add(procedimentoId);
        }
    }
}
