
package com.praticamodular.HospManager.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ProfissionalSaude extends Pessoa {

    protected String registroProfissional;
    protected List<Disponibilidade> disponibilidades = new ArrayList<>();

    public boolean estaDisponivel(Disponibilidade disponibilidade) {
        if (disponibilidades == null) {
            return false;
        }

        return disponibilidades.contains(disponibilidade);
    }

    public boolean possuiConflito() {
        if (disponibilidades == null) {
            return false;
        }

        for (int i = 0; i < disponibilidades.size(); i++) {
            for (int j = i + 1; j < disponibilidades.size(); j++) {

                if (disponibilidades.get(i)
                        .conflitaCom(disponibilidades.get(j))) {
                    return true;
                }
            }
        }

        return false;
    }

    public abstract String getAtuacao();
}
