package com.rica.publicaciones_service.publicaciones;
import com.rica.publicaciones_service.compartido.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class PublicacionServiceTest {
    PublicacionRepository repo = mock(PublicacionRepository.class);
    VerificadorInvestigador verificador = mock(VerificadorInvestigador.class);
    PublicacionService servicio = new PublicacionService(repo, verificador, new LimitePublicacionesAnualesService(repo));
    Publicacion nueva() { var p=new Publicacion(); p.setInvestigadorCorreo("ana@uptc.edu.co"); p.setAnio(2026); return p; }
    @Test void desconocidoNoSeGuarda() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.registrar(nueva())); verify(repo,never()).save(any());
    }
    @Test void indisponibilidadNoSeConfundeConInexistencia() {
        when(verificador.existe(anyString())).thenThrow(new ServicioNoDisponibleException("Caído",null));
        assertThrows(ServicioNoDisponibleException.class, () -> servicio.registrar(nueva())); verify(repo,never()).save(any());
    }
    @Test void respetaLimiteAnual() {
        when(verificador.existe(anyString())).thenReturn(true);
        when(repo.countByInvestigadorCorreoAndAnio("ana@uptc.edu.co",2026)).thenReturn(5L);
        assertThrows(LimiteAnualExcedidoException.class, () -> servicio.registrar(nueva())); verify(repo,never()).save(any());
    }
    @Test void validoConCupoSeGuarda() {
        when(verificador.existe(anyString())).thenReturn(true); var p=nueva(); when(repo.save(p)).thenReturn(p);
        assertSame(p,servicio.registrar(p));
    }
    @Test void respuestaConservaIdCorreoYEstadoHuerfana() {
        var p=nueva(); p.setId("mongo-id"); p.marcarComoHuerfana(); var dto=PublicacionMapper.aResponse(p);
        assertEquals("mongo-id",dto.getId()); assertEquals("ana@uptc.edu.co",dto.getInvestigadorCorreo()); assertTrue(dto.isHuerfana());
    }
}
