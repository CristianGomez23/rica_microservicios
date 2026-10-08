package com.rica.investigadores_service.infraestructura.entrada.grpc;
import com.rica.investigadores_service.aplication.RepositorioInvestigadores;
import com.rica.investigadores_service.grpc.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
class InvestigadorGrpcServiceTest {
    @Test void respondeSegunRepositorio() {
        var repo = mock(RepositorioInvestigadores.class);
        when(repo.existeCorreo("ana@uptc.edu.co")).thenReturn(true);
        var service = new InvestigadorGrpcService(repo);
        StreamObserver<InvestigadorResponse> observer = mock(StreamObserver.class);
        service.existeInvestigador(InvestigadorRequest.newBuilder().setCorreoInstitucional("ana@uptc.edu.co").build(),observer);
        verify(observer).onNext(InvestigadorResponse.newBuilder().setExiste(true).build());
        verify(observer).onCompleted();
        StreamObserver<InvestigadorResponse> missing = mock(StreamObserver.class);
        service.existeInvestigador(InvestigadorRequest.newBuilder().setCorreoInstitucional("nadie@uptc.edu.co").build(),missing);
        verify(missing).onNext(InvestigadorResponse.getDefaultInstance()); verify(missing).onCompleted();
    }
}
