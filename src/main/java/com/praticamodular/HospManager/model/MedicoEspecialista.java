
package com.praticamodular.HospManager.model;

import com.praticamodular.HospManager.model.enums.Especialidade;

public class MedicoEspecialista extends ProfissionalSaude {

    public MedicoEspecialista() {
    }

    public MedicoEspecialista(Especialidade especialidade) {
        setEspecialidade(especialidade);
    }
}
