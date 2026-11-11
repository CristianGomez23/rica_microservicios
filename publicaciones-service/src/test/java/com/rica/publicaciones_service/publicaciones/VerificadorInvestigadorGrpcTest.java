package com.rica.publicaciones_service.publicaciones;
import com.rica.publicaciones_service.grpc.*;
import io.grpc.*;
import io.grpc.inprocess.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;
class VerificadorInvestigadorGrpcTest {
    Server server; ManagedChannel channel; VerificadorInvestigadorGrpc verificador;
    AtomicBoolean fallar = new AtomicBoolean(); AtomicInteger llamadas = new AtomicInteger();
    @BeforeEach void iniciar() throws Exception {
        String name = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(name).directExecutor().addService(new InvestigadorGrpcGrpc.InvestigadorGrpcImplBase() {
            @Override public void existeInvestigador(InvestigadorRequest r, StreamObserver<InvestigadorResponse> o) {
                llamadas.incrementAndGet();
                if (fallar.get()) { o.onError(Status.UNAVAILABLE.asRuntimeException()); return; }
                o.onNext(InvestigadorResponse.newBuilder().setExiste(r.getCorreoInstitucional().equals("ana@uptc.edu.co")).build());
                o.onCompleted();
            }
        }).build().start();
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
        verificador = new VerificadorInvestigadorGrpc(InvestigadorGrpcGrpc.newBlockingStub(channel));
    }
    @AfterEach void cerrar() { channel.shutdownNow(); server.shutdownNow(); }
    @Test void consultaRealDistingueExistenteYDesconocido() {
        assertTrue(verificador.existe("ana@uptc.edu.co"));
        for(int i=0;i<12;i++) assertFalse(verificador.existe("nadie@uptc.edu.co"));
        assertEquals(13,llamadas.get());
    }
    @Test void cincoFallosAbrenCircuitoYLaSiguienteNoUsaRed() {
        fallar.set(true);
        for(int i=0;i<5;i++) assertThrows(ServicioNoDisponibleException.class, () -> verificador.existe("ana@uptc.edu.co"));
        var ex = assertThrows(ServicioNoDisponibleException.class, () -> verificador.existe("ana@uptc.edu.co"));
        assertTrue(ex.getMessage().contains("circuito abierto")); assertEquals(5,llamadas.get());
    }
}
