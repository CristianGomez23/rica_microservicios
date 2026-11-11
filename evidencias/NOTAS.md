### Lenguaje Ubicuo: auditoría rápida

Antes de tocar una línea de código, léete InvestigadorController, InvestigadorRequest e InvestigadorResponse completos y responde, por escrito, en un comentario o en un archivo NOTAS.md nuevo:

1. ¿Hay algún campo o método con un nombre genérico (data, info, value, item) que debería tener un nombre del dominio?

- Rta: No, no hay algún campo o método con un nombre genérico que necesite un cambio.

2. ¿correoInstitucional y grupoInvestigacion son términos que reconocería alguien de la Facultad sin que se los tradujeran?

- Rta: Si, son términos que se reconocerian facilmente sin necesitar alguna tracuccion.


3. En Publicacion, el campo se llama investigadorCorreo, no investigadorId ni autorId. Escribe una frase explicando por qué ese nombre es más preciso para el Lenguaje Ubicuo de este dominio que una alternativa genérica como refId.

- Rta: Este nombre es mas preciso ya que para el lenguaje ubicuo ya que cualquier persona, no necesariamente un desarrollador o experto, reconoceria facilmente.

---

### Límite del Agregado: documentarlo, no inventarlo

1. ¿Cuál es la raíz del Agregado Investigador?
- Rta:  La raíz del agregado es la clase Investigador. Al ser la única clase anotada con @Entity en su respectivo paquete, es la responsable de garantizar la integridad de todos los datos que viven dentro de ella y actúa como la única puerta de entrada para modificar su estado.

2. ¿Qué vive dentro del límite?
- Rta: Dentro de este límite transaccional viven los atributos que pertenecen intrínsecamente al investigador: id, nombre completo, correo institucional y grupo de investigacion.

3. ¿Por qué Publicacion NO está dentro de este límite?
- Rta: Para cumplir con la regla de "Agregados pequeños". Un investigador a lo largo de su carrera puede tener decenas o cientos de publicaciones. Si metemos esa colección dentro del Agregado Investigador, cada vez que se actualice el nombre o el grupo del investigador, la base de datos tendría que cargar y gestionar toda esa enorme lista de publicaciones en memoria. Mantenerlos separados optimiza el rendimiento.

4. ¿Qué pasaría si alguien agrega un campo List<Publicacion> publicaciones directo en Investigador?
- Rta: Rompería el aislamiento de las transacciones y generaría un alto acoplamiento. Por ejemplo, si dos procesos intentan registrar una nueva publicación para el mismo investigador al mismo tiempo, ocurriría un conflicto de concurrencia donde una transacción podría sobreescribir a la otra. Además, al referenciar la publicación solo por el String del correo (identidad), se mantiene la arquitectura lo suficientemente desacoplada como para poder separar ambos dominios en distintos servicios en el futuro si fuera necesario.
---
### Auditoría de puertos y adaptadores existentes
1. InvestigadorRepository es una interfaz, nunca una clase concreta, desde el Tutorial 4. Según la guía (sección 3), ¿es un puerto primario o secundario? Justifica con una frase: ¿quién inicia la llamada, el núcleo o algo externo?
- Rta: Es un puerto secundario, ya que el nucleo es quien inicia la llamada.

2. InvestigadorController — ¿es un adaptador primario o secundario? ¿Qué tecnología concreta envuelve?
- Rta: Es un adaptador primario, especificamente un controlador REST, que recibe las peticiones http y traduce la llamada al puerto.

3. InvestigadorService hoy es una clase concreta, no una interfaz. InvestigadorController la llama directamente. Según la nota de la guía (sección 6, "Lo que sí falta hoy en rica-api"), ¿qué pieza falta para que exista un puerto primario explícito?
- Falta una interfaz que formalice el puerto, delimitando lo que se promete en el caso de uso y lo que cumple InvestigadorService.

4. InvestigadorFactory del Taller de la Lección 3 — ¿pertenece al núcleo o a un adaptador? Pista: revisa sus import (sección 5 de la guía, "La prueba del núcleo limpio").
- Rta: La clase pertenece al nucleo, como una fuga controlada al tener la anotación @Component.
---
### El corte: qué va en cada servicio
| Paquete / clase actual | Destino |
| :--- | :--- |
| `investigadores.*` (dominio, aplicación, infraestructura) | `investigadores-service` |
| `publicaciones.*` | `publicaciones-service` |
| `compartido.GlobalExceptionHandler`, `RecursoNoEncontradoException` | Se duplica en ambos — cada servicio necesita su propio manejo de errores; ya no hay un único `@ControllerAdvice` compartido |
| `plataforma.StatusController`, `CorsConfig` | Se duplica en ambos, adaptado (cada servicio expone su propio `/api/status`) |
| `plataforma.ArranqueInformativo`, `SaludoInstitucionalService` | Se descarta — era una demostración del Tutorial 2 (IoC/DI) sin valor de negocio real; no vale la pena mantenerlo en dos servicios |