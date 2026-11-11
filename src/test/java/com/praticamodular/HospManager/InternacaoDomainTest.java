
package com.praticamodular.HospManager;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import com.praticamodular.HospManager.exception.RegraNegocioException;
import com.praticamodular.HospManager.model.Internacao;
import com.praticamodular.HospManager.model.Quarto;
import com.praticamodular.HospManager.model.enums.SituacaoQuarto;
import com.praticamodular.HospManager.model.enums.StatusInternacao;

class InternacaoDomainTest {
    @Test
    void altaEncerraInternacaoERegistraData() {
        Internacao internacao = new Internacao();
        internacao.setDataEntrada(LocalDate.of(2026, 11, 1));
        internacao.setStatus(StatusInternacao.ATIVA);

        internacao.registrarAlta(LocalDate.of(2026, 11, 10));

        assertEquals(StatusInternacao.ENCERRADA, internacao.getStatus());
        assertEquals(LocalDate.of(2026, 11, 10), internacao.getDataEfetivaAlta());
        assertFalse(internacao.estaAtiva());
    }

    @Test
    void naoPermiteAltaAnteriorAEntrada() {
        Internacao internacao = new Internacao();
        internacao.setDataEntrada(LocalDate.of(2026, 11, 10));
        internacao.setStatus(StatusInternacao.ATIVA);

        assertThrows(RegraNegocioException.class,
                () -> internacao.registrarAlta(LocalDate.of(2026, 11, 9)));
    }

    @Test
    void naoPermiteRegistrarAltaDuasVezes() {
        Internacao internacao = new Internacao();
        internacao.setDataEntrada(LocalDate.of(2026, 11, 1));
        internacao.setStatus(StatusInternacao.ATIVA);
        internacao.registrarAlta(LocalDate.of(2026, 11, 10));

        assertThrows(RegraNegocioException.class,
                () -> internacao.registrarAlta(LocalDate.of(2026, 11, 11)));
    }

    @Test
    void quartoMudaSituacaoDeAcordoComOcupacao() {
        Quarto quarto = new Quarto();
        quarto.setCapacidadeMaxima(2);

        quarto.setPacientesInternados(1);
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
        assertTrue(quarto.possuiVaga());
        assertEquals(1, quarto.vagasDisponiveis());

        quarto.setPacientesInternados(2);
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
        assertFalse(quarto.possuiVaga());
        assertEquals(0, quarto.vagasDisponiveis());
    }
}
