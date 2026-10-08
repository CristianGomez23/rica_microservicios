package com.rica.investigadores_service;
import com.rica.investigadores_service.aplication.InvestigadorUseCase;
import com.rica.investigadores_service.infraestructura.salida.persistencia.InvestigadorRepository;
import com.rica.investigadores_service.infraestructura.salida.mensajeria.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
@SpringBootTest @ActiveProfiles("test")
class OutboxTransaccionTest {
    @Autowired InvestigadorUseCase servicio;
    @Autowired InvestigadorRepository investigadores;
    @MockitoSpyBean OutboxEventRepository outbox;
    @Test void registroYEventoSeConfirmanJuntos() {
        var investigador = servicio.registrar("Nombre con \"comillas\"", "atomicidad@uptc.edu.co", "Grupo");
        assertTrue(investigadores.existsById(investigador.getId()));
        assertTrue(outbox.findAll().stream().anyMatch(e -> e.getClave().equals("atomicidad@uptc.edu.co") && !e.isPublicado()));
    }
    @Test void falloEnOutboxRevierteElInvestigador() {
        doThrow(new IllegalStateException("fallo escritura outbox")).when(outbox).save(any());
        assertThrows(IllegalStateException.class, () -> servicio.registrar("Ana", "rollback@uptc.edu.co", "Grupo"));
        assertFalse(investigadores.existsByCorreoInstitucional_Valor("rollback@uptc.edu.co"));
    }
}
