package com.rica.investigadores_service.aplication;

import java.util.Map;

public interface PublicadorEventos {

    void publicar(String topico, String clave, Map<String, Object> contenido);
}
