package com.praticamodular.HospManager.repository;

import java.util.Optional;
import com.praticamodular.HospManager.model.HistoricoMedico;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoricoMedicoRepository extends MongoRepository<HistoricoMedico, String> {
    Optional<HistoricoMedico> findByPacienteId(String pacienteId);
}
