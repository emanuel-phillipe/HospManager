package com.praticamodular.HospManager.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.praticamodular.HospManager.dto.*;
import com.praticamodular.HospManager.model.enums.StatusInternacao;
import com.praticamodular.HospManager.service.InternacaoService;

@RestController
@RequestMapping("/internacoes")
@Tag(name = "Internações", description = "Admissão, acompanhamento e alta hospitalar")
public class InternacaoController {
    private final InternacaoService service;

    public InternacaoController(InternacaoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar internação e reservar vaga no quarto")
    public ResponseEntity<InternacaoResponse> criar(@Valid @RequestBody InternacaoRequest dados) {
        InternacaoResponse internacao = service.criar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(internacao.id()).toUri();
        return ResponseEntity.created(location).body(internacao);
    }

    @GetMapping
    @Operation(summary = "Listar internações, com filtros opcionais")
    public List<InternacaoResponse> listar(
            @RequestParam(required = false) StatusInternacao status,
            @RequestParam(required = false) String pacienteId) {
        return service.listar(status, pacienteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar internação por ID")
    public InternacaoResponse buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar internação ativa")
    public InternacaoResponse atualizar(@PathVariable String id, @Valid @RequestBody InternacaoRequest dados) {
        return service.atualizar(id, dados);
    }

    @PatchMapping("/{id}/alta")
    @Operation(summary = "Registrar alta e liberar vaga no quarto")
    public InternacaoResponse registrarAlta(@PathVariable String id, @Valid @RequestBody AltaRequest dados) {
        return service.registrarAlta(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir internação encerrada")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
