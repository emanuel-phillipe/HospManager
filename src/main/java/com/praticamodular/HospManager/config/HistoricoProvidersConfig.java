package com.praticamodular.HospManager.config;

import java.util.List;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.praticamodular.HospManager.service.*;

// Registrada depois dos beans dos módulos, permitindo substituir cada porta.
@Component
@AutoConfiguration
public class HistoricoProvidersConfig {
    @Bean
    @ConditionalOnMissingBean(AtendimentoHistoricoProvider.class)
    public AtendimentoHistoricoProvider atendimentoHistoricoProvider() {
        return pacienteId -> List.of();
    }

    @Bean
    @ConditionalOnMissingBean(InternacaoHistoricoProvider.class)
    public InternacaoHistoricoProvider internacaoHistoricoProvider() {
        return pacienteId -> List.of();
    }
}
    