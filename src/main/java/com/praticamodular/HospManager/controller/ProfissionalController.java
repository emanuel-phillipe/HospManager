
package com.praticamodular.HospManager.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.praticamodular.HospManager.dto.ProcedimentoIdRequest;
import com.praticamodular.HospManager.dto.ProfissionalRequest;
import com.praticamodular.HospManager.dto.ProfissionalResponse;
import com.praticamodular.HospManager.model.enums.Especialidade;
import com.praticamodular.HospManager.service.ProfissionalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalService service;

    public ProfissionalController(ProfissionalService service) {
        this.service = service;
    }

    // Cadastra um novo profissional
    @PostMapping
    public ResponseEntity<ProfissionalResponse> criar(
            @Valid @RequestBody ProfissionalRequest dados) {

        ProfissionalResponse resposta = service.criar(dados);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    // Lista profissionais e permite filtrar por especialidade
    @GetMapping
    public Page<ProfissionalResponse> listar(
            @RequestParam(required = false)
            Especialidade especialidade,
            Pageable pageable) {

        return service.listar(especialidade, pageable);
    }

    // Busca um profissional pelo ID
    @GetMapping("/{id}")
    public ProfissionalResponse buscar(@PathVariable String id) {
        return service.buscar(id);
    }

    // Atualiza os dados de um profissional
    @PutMapping("/{id}")
    public ProfissionalResponse atualizar(
            @PathVariable String id,
            @Valid @RequestBody ProfissionalRequest dados) {

        return service.atualizar(id, dados);
    }

    // Desativa um profissional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }


    // Registra um procedimento para um cirurgião
    @PostMapping("/{id}/procedimentos")
    public ResponseEntity<Void> registrarProcedimento(
            @PathVariable String id,
            @Valid @RequestBody ProcedimentoIdRequest dados) {

        service.registrarProcedimento(
            id,
            dados.procedimentoId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    // Lista os procedimentos associados ao cirurgião
    @GetMapping("/{id}/procedimentos")
    public java.util.List<String> listarProcedimentos(
            @PathVariable String id) {

        return service.listarProcedimentos(id);
    }

}
