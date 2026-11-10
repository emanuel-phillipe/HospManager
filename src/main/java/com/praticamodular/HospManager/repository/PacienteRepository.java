package com.praticamodular.HospManager.repository;

import com.praticamodular.HospManager.model.Paciente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacienteRepository extends MongoRepository<Paciente, String> {
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, String id);
    Page<Paciente> findByAtivoTrue(Pageable pageable);
    Page<Paciente> findByAtivoTrueAndNomeContainingIgnoreCase(String nome, Pageable pageable);
}
