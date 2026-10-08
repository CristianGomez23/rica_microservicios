package com.rica.publicaciones_service.compartido;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
public class KafkaConfiguration {
    @Bean
    DefaultErrorHandler kafkaErrorHandler() {
        // Un fallo transitorio no debe descartar una confirmación de la Saga.
        return new DefaultErrorHandler(new FixedBackOff(2000L, FixedBackOff.UNLIMITED_ATTEMPTS));
    }
}
