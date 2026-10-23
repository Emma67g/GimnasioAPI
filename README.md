# FitZone API: inscripción a clases grupales

API REST basada en SOA que implementa el diseño del Reto 1 de la Unidad 2 "Especifica los servicios de un gimnasio".
Spring Boot 4.1.1 · Java 21 · Maven · Spring Web · Spring Data JPA · MySQL.

## Estructura

| Paquete | Contenido |
|---|---|
| `controllers` | Endpoints REST (`ClaseController`, `MembresiaController`, `InscripcionController`, `NotificacionController`, `InscribirClaseController`) y `GlobalExceptionHandler`, que convierte los errores en JSON |
| `models` | Entidades JPA (`Clase`, `Membresia`, `Inscripcion`, `Notificacion`), enums y `CodigoError` |
| `repositories` | Interfaces Spring Data JPA |
| `services` | Un servicio por cada servicio del diseño: `ClaseService`, `MembresiaService`, `InscripcionService` (entidad), `NotificacionService` (utilidad), `InscribirClaseService` (tarea). Incluye `ApiException` y `DataInitializer` (datos de prueba) |
| `dtos` | Mensajes de entrada y salida (records) |

## Endpoints

| Capacidad del diseño | Endpoint |
|---|---|
| `inscribirEnClase` | `POST /api/inscribir-clase` |
| `consultarClases` | `GET /api/clases?sucursal=CENTRO&disciplina=YOGA&fecha=2026-10-20` (filtros opcionales) |
| `consultarCupo` | `GET /api/clases/{idClase}/cupo` |
| `actualizarCupo` | `PATCH /api/clases/{idClase}/cupo` con `{"operacion": "RESERVAR"}` o `"LIBERAR"` |
| `consultarMembresia` | `GET /api/membresias/{numeroSocio}` |
| `registrarInscripcion` | Interna (solo la invoca `InscribirClase`). Consultas: `GET /api/inscripciones/{folio}` y `GET /api/inscripciones/socio/{numeroSocio}` |
| `enviarConfirmacion`, `programarRecordatorio` | Internas. Consulta de estado: `GET /api/notificaciones/socio/{numeroSocio}` |

## Sucursales

FitZone tiene tres sucursales, modeladas con el enum `Sucursal`: `NORTE`, `CENTRO` y `SUR`. Cada clase pertenece a una sucursal y el cupo de 20 se cuenta por clase. Un valor distinto en el filtro responde `DATOS_INVALIDOS` (400); escríbelo en mayúsculas.

## Instrucciones para ejecución local

### Requisitos previos

- JDK 21
- Git
- MySQL 8 en ejecución
- Postman (para probar la API)

### Instalación

1. **Clonar el repositorio**

   ```bash
   git clone https://github.com/Emma67g/GimnasioAPI.git
   cd gimnasio
   ```

2. **Preparar la base de datos.** No es necesario crearla a mano: la URL de conexión incluye `createDatabaseIfNotExist=true`, por lo que la base `fitzone` se crea al iniciar. Solo asegúrate de que MySQL esté corriendo y de tener un usuario con permisos.

3. **Configurar las variables de entorno** 

La aplicación lee su configuración desde variables de entorno, con valores por defecto.

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/fitzone?createDatabaseIfNotExist=true&serverTimezone=UTC` |
| `DB_USERNAME` | Usuario de MySQL | *(obligatorio)* |
| `DB_PASSWORD` | Contraseña de MySQL | *(obligatorio)* |
| `SERVER_PORT` | Puerto de la API | `8080 (predeterminado)` |

4. **Ejecutar la aplicación**

   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

   También puedes generar el `.jar` y ejecutarlo:

   ```bash
   ./mvnw clean package
   java -jar target/*.jar
   ```

## Datos de prueba

Socios: `FZ000001` (vigente), `FZ000002` (vencida), `FZ000003` (vigente).
Clases (ids): 1 Yoga (Centro), 2 Spinning (Norte), 3 Funcional (Sur), 4 Spinning (Centro) con los 20 lugares ocupados.

Si ya habías ejecutado una versión anterior, borra la base antes de arrancar: `DROP DATABASE fitzone;`

## Pruebas rápidas

```bash
# Inscripción correcta (201)
curl -X POST localhost:8080/api/inscribir-clase -H "Content-Type: application/json" \
     -d '{"numeroSocio":"FZ000001","idClase":1}'

# Mismos datos otra vez: INSCRIPCION_DUPLICADA (409)
# Socio con membresía vencida: {"numeroSocio":"FZ000002","idClase":1} -> MEMBRESIA_VENCIDA (403)
# Clase llena: {"numeroSocio":"FZ000001","idClase":4} -> CLASE_SIN_CUPO (409)
# Clase inexistente: idClase 999 -> CLASE_NO_ENCONTRADA (404)

curl localhost:8080/api/clases/1/cupo
curl localhost:8080/api/notificaciones/socio/FZ000001   # confirmación y recordatorio
```

Todos los errores responden con `{"codigo": "...", "mensaje": "..."}`.

## Decisiones de diseño

- **Sin sobrecupo:** `actualizarCupo` descuenta el lugar con un `UPDATE ... WHERE lugaresOcupados < capacidadMaxima`, así que dos peticiones simultáneas nunca superan 20.
- **Todo o nada:** `inscribirEnClase` corre en una sola transacción. Si falla el registro, el lugar reservado y los mensajes se deshacen.
- **Sin inscripciones duplicadas:** se valida antes y además hay una restricción única `(numero_socio, clase_id)`.
- **Mensajes asíncronos:** `enviarConfirmacion` y `programarRecordatorio` solo guardan el mensaje como `PENDIENTE` (tabla `notificaciones`) y regresan. `NotificacionService.despacharPendientes` corre cada minuto y "envía" los que ya vencieron (por ahora con un log; ahí se conectaría un proveedor de correo). El recordatorio se programa 2 horas antes de la clase, o de inmediato si faltan menos de 2 horas.
- **Sin broker externo:** la tabla `notificaciones` cumple el papel de la cola porque el proyecto solo usa MySQL, JPA y Web. El PDF propone RabbitMQ como evolución.
