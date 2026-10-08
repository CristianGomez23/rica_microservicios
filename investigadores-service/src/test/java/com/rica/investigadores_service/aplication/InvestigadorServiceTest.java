package com.rica.investigadores_service.aplication;
import com.rica.investigadores_service.dominio.*;
import com.rica.investigadores_service.compartido.RecursoNoEncontradoException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class InvestigadorServiceTest {
    @Mock RepositorioInvestigadores repositorio;
    @Mock PublicadorEventos eventos;
    InvestigadorService servicio;
    Investigador investigador;
    @BeforeEach void preparar() {
        servicio = new InvestigadorService(repositorio, new InvestigadorFactory(repositorio), eventos);
        investigador = new Investigador(7L, "Ana", new CorreoInstitucional("ana@uptc.edu.co"), "Grupo");
    }
    @Test void registrarGuardaYPublicaEvento() {
        when(repositorio.guardar(any())).thenAnswer(i -> { Investigador x=i.getArgument(0); x.setId(7L); return x; });
        assertEquals(7L, servicio.registrar("Ana", "ana@uptc.edu.co", "Grupo").getId());
        verify(eventos).publicar("investigadores.registrados", "ana@uptc.edu.co",
                Map.of("id",7L,"correoInstitucional","ana@uptc.edu.co","nombreCompleto","Ana"));
    }
    @Test void duplicadoNoGuardaNiPublica() {
        when(repositorio.existeCorreo("ana@uptc.edu.co")).thenReturn(true);
        assertThrows(CorreoDuplicadoException.class, () -> servicio.registrar("Ana","ana@uptc.edu.co","Grupo"));
        verify(repositorio, never()).guardar(any()); verifyNoInteractions(eventos);
    }
    @Test void eliminarInexistenteDaNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.iniciarEliminacion(99L));
        verifyNoInteractions(eventos);
    }
    @Test void iniciarEliminacionEsIdempotente() {
        when(repositorio.buscarPorId(7L)).thenReturn(Optional.of(investigador));
        servicio.iniciarEliminacion(7L); servicio.iniciarEliminacion(7L);
        assertTrue(investigador.estaEnEliminacion());
        verify(repositorio).guardar(investigador);
        verify(eventos).publicar("investigadores.en-eliminacion","ana@uptc.edu.co", Map.of("correoInstitucional","ana@uptc.edu.co"));
        verify(repositorio, never()).eliminar(any());
    }
    @Test void confirmarBorraSoloSiEstaEnEliminacion() {
        investigador.marcarEnEliminacion();
        when(repositorio.buscarPorCorreo("ana@uptc.edu.co")).thenReturn(Optional.of(investigador));
        servicio.confirmarEliminacion("ana@uptc.edu.co"); verify(repositorio).eliminar(investigador);
    }
    @Test void confirmacionNoBorraInvestigadorActivo() {
        when(repositorio.buscarPorCorreo("ana@uptc.edu.co")).thenReturn(Optional.of(investigador));
        servicio.confirmarEliminacion("ana@uptc.edu.co"); verify(repositorio, never()).eliminar(any());
    }
    @Test void confirmacionRepetidaSinInvestigadorNoFalla() {
        servicio.confirmarEliminacion("ana@uptc.edu.co"); verify(repositorio, never()).eliminar(any());
    }
}
