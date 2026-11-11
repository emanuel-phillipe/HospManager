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
import com.praticamodular.HospManager.service.QuartoService;

@RestController
@RequestMapping("/quartos")
@Tag(name = "Quartos", description = "Cadastro de quartos e controle de vagas")
public class QuartoController {
    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastrar quarto")
    public ResponseEntity<QuartoResponse> criar(@Valid @RequestBody QuartoRequest dados) {
        QuartoResponse quarto = service.criar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(quarto.id()).toUri();
        return ResponseEntity.created(location).body(quarto);
    }

    @GetMapping
    @Operation(summary = "Listar quartos e ocupação atual")
    public List<QuartoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar quartos que possuem vagas")
    public List<QuartoResponse> listarDisponiveis() {
        return service.listarDisponiveis();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar quarto e verificar vagas")
    public QuartoResponse buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar quarto")
    public QuartoResponse atualizar(@PathVariable String id, @Valid @RequestBody QuartoRequest dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir quarto sem internações ativas")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
