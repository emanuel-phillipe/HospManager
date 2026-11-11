
package com.praticamodular.HospManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.praticamodular.HospManager.dto.InternacaoRequest;
import com.praticamodular.HospManager.exception.ConflitoException;
import com.praticamodular.HospManager.model.Internacao;
import com.praticamodular.HospManager.model.Paciente;
import com.praticamodular.HospManager.model.Quarto;
import com.praticamodular.HospManager.model.enums.StatusInternacao;
import com.praticamodular.HospManager.repository.InternacaoRepository;
import com.praticamodular.HospManager.repository.PacienteRepository;
import com.praticamodular.HospManager.repository.QuartoRepository;
import com.praticamodular.HospManager.service.InternacaoService;
import com.praticamodular.HospManager.service.QuartoService;

@ExtendWith(MockitoExtension.class)
class InternacaoServiceTest {

    @Mock
    InternacaoRepository internacoes;

    @Mock
    PacienteRepository pacientes;

    @Mock
    QuartoRepository quartos;

    @Mock
    QuartoService quartoService;

    @InjectMocks
    InternacaoService service;

    private Paciente paciente;
    private Quarto quarto;
    private InternacaoRequest dados;

    @BeforeEach
    void preparar() {
        paciente = new Paciente();
        paciente.setId("paciente-1");
        paciente.setNome("Paciente Teste");
        paciente.setAtivo(true);

        quarto = new Quarto();
        quarto.setId("quarto-1");
        quarto.setNumero(101);
        quarto.setCapacidadeMaxima(2);

        dados = new InternacaoRequest(
                "paciente-1",
                "profissional-1",
                "quarto-1",
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 15),
                "Observação"
        );
    }

    @Test
    void criaInternacaoQuandoExisteVaga() {

        when(pacientes.findById("paciente-1"))
                .thenReturn(Optional.of(paciente));

        when(internacoes.existsByPacienteIdAndStatus(
                "paciente-1", StatusInternacao.ATIVA))
                .thenReturn(false);

        when(quartoService.encontrar("quarto-1"))
                .thenReturn(quarto);

        when(internacoes.countByQuartoIdAndStatus(
                "quarto-1", StatusInternacao.ATIVA))
                .thenReturn(1L);

        when(internacoes.insert(any(Internacao.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(quartoService.ocupacaoAtual("quarto-1"))
                .thenReturn(2);

        when(quartos.findById("quarto-1"))
                .thenReturn(Optional.of(quarto));

        var resposta = service.criar(dados);

        assertEquals("paciente-1", resposta.pacienteId());
        assertEquals("quarto-1", resposta.quartoId());
        assertEquals(StatusInternacao.ATIVA, resposta.status());

        verify(internacoes).insert(any(Internacao.class));
        verify(quartos).save(quarto);
    }

    @Test
    void bloqueiaInternacaoQuandoQuartoEstaLotado() {

        when(pacientes.findById("paciente-1"))
                .thenReturn(Optional.of(paciente));

        when(internacoes.existsByPacienteIdAndStatus(
                "paciente-1", StatusInternacao.ATIVA))
                .thenReturn(false);

        when(quartoService.encontrar("quarto-1"))
                .thenReturn(quarto);

        when(internacoes.countByQuartoIdAndStatus(
                "quarto-1", StatusInternacao.ATIVA))
                .thenReturn(2L);

        assertThrows(
                ConflitoException.class,
                () -> service.criar(dados)
        );

        verify(internacoes, never())
                .insert(any(Internacao.class));
    }
}
