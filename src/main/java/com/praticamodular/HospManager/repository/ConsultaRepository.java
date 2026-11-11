package com.praticamodular.HospManager.repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.praticamodular.HospManager.model.Consulta;
import com.praticamodular.HospManager.model.enums.StatusConsulta;

public interface ConsultaRepository extends MongoRepository<Consulta, String> {
    @Query("{ 'medicoId': ?0 }")
    List<Consulta> findByMedicoId(String medicoId);

    @Query(value = "{ 'medicoId': ?0, 'data': ?1, 'horario': ?2, 'status': { $in: ?3 } }", exists = true)
    boolean existeConflitoMedico(String medicoId, LocalDate data, LocalTime horario, Collection<StatusConsulta> status);

    boolean existsByPacienteIdAndDataAndHorarioAndStatusIn(String pacienteId, LocalDate data, LocalTime horario,
            Collection<StatusConsulta> status);

    List<Consulta> findByPacienteIdOrderByDataDescHorarioDesc(String pacienteId);
}
