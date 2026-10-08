package com.rica.publicaciones_service.publicaciones;
import org.springframework.stereotype.Service;
@Service
public class LimitePublicacionesAnualesService {
    private static final int MAXIMO_POR_ANIO = 5;
    private final PublicacionRepository repositorio;
    public LimitePublicacionesAnualesService(PublicacionRepository repositorio) { this.repositorio = repositorio; }
    public boolean puedeRegistrar(Publicacion nueva) {
        return repositorio.countByInvestigadorCorreoAndAnio(nueva.getInvestigadorCorreo(), nueva.getAnio()) < MAXIMO_POR_ANIO;
    }
}
