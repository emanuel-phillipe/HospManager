
package com.praticamodular.HospManager.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.praticamodular.HospManager.model.ProfissionalSaude;
import com.praticamodular.HospManager.model.enums.Especialidade;

public interface ProfissionalRepository
        extends MongoRepository<ProfissionalSaude, String> {

    Page<ProfissionalSaude> findByAtivoTrue(
        Pageable pageable
    );

    Page<ProfissionalSaude> findByAtivoTrueAndEspecialidade(
        Especialidade especialidade,
        Pageable pageable
    );

    boolean existsByRegistroProfissional(
        String registroProfissional
    );

    boolean existsByRegistroProfissionalAndIdNot(
        String registroProfissional,
        String id
    );
}
