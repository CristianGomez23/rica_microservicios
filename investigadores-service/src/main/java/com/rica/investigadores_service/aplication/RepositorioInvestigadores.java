package com.rica.investigadores_service.aplication;

import com.rica.investigadores_service.dominio.Investigador;

import java.util.List;
import java.util.Optional;

public interface RepositorioInvestigadores {
    List<Investigador> listarTodos();

    Optional<Investigador> buscarPorId(Long id);

    boolean existeCorreo(String correoInstitucional);

    Investigador guardar(Investigador investigador);
    Optional<Investigador> buscarPorCorreo(String correoInstitucional);
    void eliminar(Investigador investigador);
}
