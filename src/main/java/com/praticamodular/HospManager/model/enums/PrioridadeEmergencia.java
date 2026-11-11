package com.praticamodular.HospManager.model.enums;



/** Classificação de risco do paciente na emergência. Quanto maior o nível, mais urgente. */
public enum PrioridadeEmergencia {
    BAIXA(1),
    MODERADA(2),
    ALTA(3),
    CRITICA(4);

    private final int nivel;

    PrioridadeEmergencia(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean maisUrgenteQue(PrioridadeEmergencia outra) {
        return this.nivel > outra.nivel;
    }
}
