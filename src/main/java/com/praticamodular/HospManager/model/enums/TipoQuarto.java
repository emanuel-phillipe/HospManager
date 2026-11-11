package com.praticamodular.HospManager.model.enums;

public enum TipoQuarto {
    ENFERMARIA, APARTAMENTO, UTI;

    public void validarCapacidade(int capacidade) {
        if (capacidade < 1) throw new com.praticamodular.HospManager.exception.RegraNegocioException(
                "Capacidade de " + this + " deve ser maior que zero");
    }
}
