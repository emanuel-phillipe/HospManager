package com.praticamodular.HospManager.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.praticamodular.HospManager.model.Internacao;
import com.praticamodular.HospManager.model.enums.StatusInternacao;

public interface InternacaoRepository extends MongoRepository<Internacao, String> {
    long countByQuartoIdAndStatus(String quartoId, StatusInternacao status);
    List<Internacao> findByPacienteIdOrderByDataEntradaDesc(String pacienteId);
    List<Internacao> findByQuartoIdAndStatus(String quartoId, StatusInternacao status);
    List<Internacao> findByStatusOrderByDataEntradaDesc(StatusInternacao status);
    boolean existsByPacienteIdAndStatus(String pacienteId, StatusInternacao status);
    boolean existsByPacienteIdAndStatusAndIdNot(String pacienteId, StatusInternacao status, String id);
}
