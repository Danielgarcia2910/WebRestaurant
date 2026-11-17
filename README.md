# WebRestaurant

Sistema para mostrar el menú de un restaurante y administrarlo (platos, categorías e ingredientes). Spring Boot, Thymeleaf, JPA, MySQL y Spring Security.

## Arrancar

    .\mvnw.cmd spring-boot:run
    http://localhost:8081

- Menú público: http://localhost:8081/menu
- Administración: http://localhost:8081/login · usuario `admin` · contraseña `admin123` (se cambian en `application.properties`)
- Base de datos MySQL (`restaurante_db`), con datos de ejemplo la primera vez. Se ve en MySQL Workbench.

## Reservas

- Los clientes reservan en `/reservas` (nombre, teléfono, fecha, hora, personas, notas).
- El personal las ve y gestiona (confirmar, cancelar, eliminar) en `/admin/reservas`.
- Horario de reservas (opcional, en `application.properties`): `restaurante.reservas.apertura=12:00` y `restaurante.reservas.cierre=22:00`.

## Base de datos (MySQL)

1. Ten MySQL corriendo en `localhost:3306`.
2. En `src/main/resources/application.properties` cambia `TU_PASSWORD_AQUI` por tu contraseña de MySQL
   (o define la variable de entorno `SPRING_DATASOURCE_PASSWORD`).
3. Arranca la app: se crea la base `restaurante_db` y las tablas solas.

No subas tu contraseña real a GitHub.

## Modelo

| Entidad | Relación |
|---|---|
| `Categoria` | uno a muchos con `Plato` |
| `Plato` | muchos a uno con `Categoria`; muchos a muchos con `Ingrediente` |
| `Ingrediente` | muchos a muchos con `Plato` |

## Reglas de negocio

1. El nombre del plato no se repite — `PlatoServiceJpa.guardar`
2. El precio debe ser mayor que 0 — `Plato` y `PlatoServiceJpa.guardar`
3. Un plato agotado se muestra marcado, no desaparece — `menu/lista.html`
4. Una categoría con platos no se elimina — `CategoriaService.eliminar`
5. El nombre de la categoría no se repite — `CategoriaService.guardar`
6. Un ingrediente en uso no se elimina y su nombre no se repite — `IngredienteService`

## Rutas

| Método | Ruta | Qué hace |
|---|---|---|
| GET | `/` | Inicio |
| GET | `/menu` · `/menu?buscar=...&categoriaId=...` | Menú agrupado por categoría, con búsqueda y filtro |
| GET | `/menu/{id}` | Detalle de un plato |
| GET | `/login` | Inicio de sesión |
| GET | `/admin/platos` | Lista de platos |
| GET | `/admin/platos/nuevo` · `/{id}/editar` | Formulario de plato |
| POST | `/admin/platos/guardar` | Crea o actualiza un plato |
| POST | `/admin/platos/{id}/disponibilidad` | Cambia entre disponible y agotado |
| POST | `/admin/platos/{id}/eliminar` | Elimina un plato |
| GET | `/admin/categorias` · `/{id}/editar` | Lista y formulario de categorías |
| POST | `/admin/categorias/guardar` · `/{id}/eliminar` | Guarda o elimina una categoría |
| GET | `/admin/ingredientes` · `/{id}/editar` | Lista y formulario de ingredientes |
| POST | `/admin/ingredientes/guardar` · `/{id}/eliminar` | Guarda o elimina un ingrediente |

### API REST (JSON)

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/platos` · `?buscar=` · `?categoriaId=` | 200 y la lista |
| GET | `/api/platos/{id}` | 200, o 404 si no existe |
| POST | `/api/platos` | 201, 400 si el JSON es inválido, 409 si se rompe una regla |
| PUT | `/api/platos/{id}` | 200, 404 o 409 |
| DELETE | `/api/platos/{id}` | 204 o 404 |

Los GET son públicos. POST, PUT y DELETE piden usuario y contraseña de administrador (autenticación básica).

## Seguridad

- `/admin/**` exige iniciar sesión con rol `ADMIN`.
- El menú y el resto de páginas son públicos.
- Los formularios con `th:action` incluyen el token CSRF automáticamente.

## Estructura

    modelo/        Plato · Categoria · Ingrediente
    repositorio/   interfaces Spring Data
    servicio/      reglas de negocio y excepciones
    controlador/   páginas, api/ y dto/
    configuracion/ seguridad, fotos y datos de ejemplo
