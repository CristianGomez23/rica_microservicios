# Lección 9 — Paso 7: ¿qué hacemos con la DLT?

1. **¿Quién se entera y cómo?** El equipo que mantiene `consultas-service` (el dueño del consumidor) debe recibir una alerta automática cuando la DLT reciba mensajes (por ejemplo, una métrica del número de mensajes en `investigadores.registrados.DLT` con una alarma > 0 que avise por chat o correo), además de un tablero con el volumen y el tipo de excepción (`kafka_dlt-exception-fqcn`). Es justo lo que da la observabilidad de la Lección 10: métricas, alertas y logs correlacionados.

2. **¿Cómo se reprocesa?** Primero se corrige la causa (código del consumidor o dato del productor) y después se reenvía el mensaje al tópico original con una herramienta aparte y controlada (un script o un endpoint de administración que lea la DLT, filtre por offset o excepción y republique), dejando registro de qué se reprocesó. Reenviarlo a ciegas haría que un mensaje que sigue roto vuelva a caer en la DLT.

3. **¿Cuándo es válido descartar?** Cuando el mensaje es irrecuperable y no representa un hecho de negocio (basura como `"esto no es json"`, pruebas, o eventos ya superados por otros posteriores). Lo decide el dueño del servicio o del dato, no el consumidor automáticamente, y la decisión debe quedar documentada (por ejemplo, en el ticket de la alerta).

4. **¿Y si es válido pero llegó a destiempo?** Reprocesarlo fuera de orden podría pisar un estado más nuevo (por ejemplo, recrear la ficha de un investigador ya eliminado). Por eso el consumidor debe ser idempotente y tolerar el desorden (comparar versiones o fechas del evento, o reconstruir la proyección desde el log), y antes de reenviarlo hay que revisar el estado actual. Las trazas y la correlación de la Lección 10 permiten ver qué pasó con esa entidad entre el fallo y el reproceso.

## Pregunta del entregable

Reintentar sin fin un mensaje venenoso bloquea la partición: ningún mensaje posterior se procesa y el sistema se degrada para todos, mientras que perderlo solo afecta a ese mensaje (aunque sin rastro). Enviarlo a una DLT es mejor que ambas opciones: libera la partición de inmediato y conserva el mensaje con su diagnóstico para corregirlo y reprocesarlo.
