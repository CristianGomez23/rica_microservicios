package com.rica.investigadores_service.infraestructura.salida.mensajeria;
import org.junit.jupiter.api.*;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class OutboxPollerTest {
    @Test void marcaPublicadoSoloTrasConfirmacion() {
        var repo = mock(OutboxEventRepository.class);
        KafkaTemplate<String,String> kafka = mock(KafkaTemplate.class);
        var evento = new OutboxEvent("registrados","ana","{}");
        when(repo.findTop50ByPublicadoFalseOrderByIdAsc()).thenReturn(List.of(evento));
        when(kafka.send("registrados","ana","{}")).thenReturn(CompletableFuture.completedFuture(null));
        new OutboxPoller(repo,kafka).publicarPendientes(); assertTrue(evento.isPublicado());
    }
    @Test void falloConservaPendienteYOrdenYPermiteReintento() {
        var repo = mock(OutboxEventRepository.class);
        KafkaTemplate<String,String> kafka = mock(KafkaTemplate.class);
        var primero = new OutboxEvent("registrados","ana","{}");
        var segundo = new OutboxEvent("eliminados","ana","{}");
        when(repo.findTop50ByPublicadoFalseOrderByIdAsc()).thenReturn(List.of(primero,segundo));
        when(kafka.send("registrados","ana","{}")).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka caído")));
        var poller = new OutboxPoller(repo,kafka); poller.publicarPendientes();
        assertFalse(primero.isPublicado()); assertFalse(segundo.isPublicado());
        verify(kafka, never()).send("eliminados","ana","{}");
        when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.completedFuture(null));
        poller.publicarPendientes(); assertTrue(primero.isPublicado()); assertTrue(segundo.isPublicado());
    }
}
