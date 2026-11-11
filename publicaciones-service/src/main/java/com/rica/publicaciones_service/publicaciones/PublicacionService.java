package com.rica.publicaciones_service.publicaciones;

import org.springframework.stereotype.Service;
import com.rica.publicaciones_service.compartido.RecursoNoEncontradoException;

import java.util.List;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final VerificadorInvestigador verificadorInvestigador;
    private final LimitePublicacionesAnualesService limiteService;
    private final PublicadorEventosPublicaciones publicadorEventos;

    public PublicacionService(PublicacionRepository publicacionRepository, VerificadorInvestigador verificadorInvestigador, LimitePublicacionesAnualesService limiteService, PublicadorEventosPublicaciones publicadorEventos) {
        this.publicacionRepository = publicacionRepository;
        this.verificadorInvestigador = verificadorInvestigador;
        this.limiteService = limiteService;
        this.publicadorEventos = publicadorEventos;
    }

    public Publicacion registrar(Publicacion publicacion) {
        if (!verificadorInvestigador.existe(publicacion.getInvestigadorCorreo())) {
            throw new RecursoNoEncontradoException("No existe un investigador con correo " + publicacion.getInvestigadorCorreo());
        }

        if (!limiteService.puedeRegistrar(publicacion)) {
            throw new com.rica.publicaciones_service.compartido.LimiteAnualExcedidoException(
                    "El investigador " + publicacion.getInvestigadorCorreo()
                            + " ya alcanzó el máximo de publicaciones registradas para " + publicacion.getAnio());
        }
        Publicacion guardada = publicacionRepository.save(publicacion);
        // Solo se anuncia el hecho cuando el comando tuvo éxito.
        publicadorEventos.publicacionRegistrada(guardada);
        return guardada;
    }

    public List<Publicacion> listarPorInvestigador(String investigadorCorreo) {
        return publicacionRepository.findByInvestigadorCorreo(investigadorCorreo);
    }

    public Publicacion buscarPorId(String id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una publicación con id " + id));
    }
}
