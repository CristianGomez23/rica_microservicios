package com.rica.investigadores_service.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rica.investigadores_service.dominio.CorreoInstitucional;
import com.rica.investigadores_service.dominio.Investigador;

public interface InvestigadorRepository extends JpaRepository<Investigador, Long> {

    boolean existsByCorreoInstitucional_Valor(String valor);

    Investigador findByCorreoInstitucional(CorreoInstitucional correoInstitucional);
    java.util.Optional<Investigador> findByCorreoInstitucional_Valor(String correoInstitucional);
}
