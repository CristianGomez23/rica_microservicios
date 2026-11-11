package com.rica.publicaciones_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"eureka.client.enabled=false", "spring.kafka.listener.auto-startup=false"})
class PublicacionesServiceApplicationTests {
    @org.springframework.test.context.bean.override.mockito.MockitoBean
    com.rica.publicaciones_service.publicaciones.PublicacionRepository publicaciones;

	@Test
	void contextLoads() {
	}

}
