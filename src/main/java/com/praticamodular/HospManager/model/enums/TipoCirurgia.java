package hospital;

public enum TipoCirurgia {
    /** Planejada com antecedência. */
    ELETIVA(true, true),
    /** Precisa ser feita em breve, mas ainda pode ser agendada. */
    URGENTE(true, true),
    /** Risco imediato de vida: feita na hora, sem preparo formal. */
    EMERGENCIAL(false, false);

    private final boolean permiteAgendamentoFuturo;
    private final boolean exigePreOperatorio;

    TipoCirurgia(boolean permiteAgendamentoFuturo, boolean exigePreOperatorio) {
        this.permiteAgendamentoFuturo = permiteAgendamentoFuturo;
        this.exigePreOperatorio = exigePreOperatorio;
    }

    public boolean permiteAgendamentoFuturo() {
        return permiteAgendamentoFuturo;
    }

    /** Se true, é obrigatório ter observações pré-operatórias para iniciar a cirurgia. */
    public boolean exigePreOperatorio() {
        return exigePreOperatorio;
    }
}
