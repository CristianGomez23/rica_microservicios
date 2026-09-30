package com.rica.publicaciones_service.publicaciones;

import org.springframework.stereotype.Service;
import com.rica.publicaciones_service.compartido.RecursoNoEncontradoException;

import java.util.List;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final VerificadorInvestigador verificadorInvestigador;
    private final LimitePublicacionesAnualesService limiteService;

    public PublicacionService(PublicacionRepository publicacionRepository, VerificadorInvestigador verificadorInvestigador, LimitePublicacionesAnualesService limiteService) {
        this.publicacionRepository = publicacionRepository;
        this.verificadorInvestigador = verificadorInvestigador;
        this.limiteService = limiteService;
    }

    public Publicacion registrar(Publicacion publicacion) {
        if (!verificadorInvestigador.existe(publicacion.getInvestigadorCorreo())) {
            throw new RecursoNoEncontradoException("No existe un investigador con correo " + publicacion.getInvestigadorCorreo());
        }

        return publicacionRepository.save(publicacion);
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