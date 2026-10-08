package com.rica.publicaciones_service.publicaciones;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class InvestigadorEnEliminacionListenerTest {
    PublicacionRepository repo = mock(PublicacionRepository.class);
    KafkaTemplate<String,String> kafka = mock(KafkaTemplate.class);
    InvestigadorEnEliminacionListener listener = new InvestigadorEnEliminacionListener(repo,kafka,new ObjectMapper());
    String mensaje = "{\"correoInstitucional\":\"ana@uptc.edu.co\"}";
    @Test void marcaAntesDeConfirmarYEsIdempotente() {
        var p = new Publicacion(); when(repo.findByInvestigadorCorreo("ana@uptc.edu.co")).thenReturn(List.of(p));
        when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.completedFuture(null));
        listener.alIniciarseEliminacion(mensaje); listener.alIniciarseEliminacion(mensaje);
        assertTrue(p.isHuerfana()); var orden=inOrder(repo,kafka);
        orden.verify(repo).saveAll(List.of(p)); orden.verify(kafka).send(eq("publicaciones.marcadas-huerfanas"),eq("ana@uptc.edu.co"),anyString());
    }
    @Test void falloAlConfirmarSePropagaParaReintentar() {
        when(repo.findByInvestigadorCorreo(anyString())).thenReturn(List.of());
        when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka caído")));
        assertThrows(IllegalStateException.class, () -> listener.alIniciarseEliminacion(mensaje));
    }
    @Test void falloDePersistenciaNoConfirma() {
        when(repo.findByInvestigadorCorreo(anyString())).thenReturn(List.of());
        doThrow(new IllegalStateException("Mongo caído")).when(repo).saveAll(any());
        assertThrows(IllegalStateException.class, () -> listener.alIniciarseEliminacion(mensaje)); verifyNoInteractions(kafka);
    }
}
