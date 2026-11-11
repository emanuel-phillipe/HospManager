package com.praticamodular.HospManager.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.praticamodular.HospManager.model.Quarto;

public interface QuartoRepository extends MongoRepository<Quarto, String> {
    boolean existsByNumero(int numero);
    boolean existsByNumeroAndIdNot(int numero, String id);
}
