# EduAirControl

EduAirControl es una plataforma web para monitoreo ambiental en espacios educativos. Permite visualizar ambientes, revisar ranking de calidad, gestionar aulas, marcar favoritos, consultar notificaciones y administrar ajustes de usuario.

El proyecto esta dividido en frontend React/Vite y backend Spring Boot con PostgreSQL.

## Stack

### Frontend

- React 19
- Vite 8
- React Router DOM 7
- i18next + react-i18next
- React Icons y Lucide React
- React Hook Form + Zod
- CSS modular por pantalla/componente

### Backend

- Java 17
- Spring Boot 4
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- OAuth2 Google
- OpenAPI / Swagger

### Infraestructura

- Docker Compose
- Nginx para servir el frontend en contenedor
- Jenkins opcional para CI/CD local

## Estructura

```text
EduAirControl/
|-- README.md
|-- Web/
|   |-- Front-End/
|   |   |-- src/
|   |   |   |-- App.jsx
|   |   |   |-- main.jsx
|   |   |   |-- context/
|   |   |   |-- modules/
|   |   |   |   |-- auth/
|   |   |   |   |-- dashboard/
|   |   |   |   |-- environment/
|   |   |   |   |-- favorites/
|   |   |   |   |-- landing/
|   |   |   |   |-- notifications/
|   |   |   |   |-- profile/
|   |   |   |   |-- ranking/
|   |   |   |   `-- settings/
|   |   |   |-- shared/
|   |   |   `-- viewmodels/
|   |   |-- package.json
|   |   |-- vite.config.js
|   |   `-- Dockerfile
|   |-- backend/
|   |   |-- src/main/java/com/eduaircontrol/backend/
|   |   |   |-- application/
|   |   |   |-- config/
|   |   |   |-- core/
|   |   |   `-- security/
|   |   |-- src/main/resources/application.properties
|   |   |-- pom.xml
|   |   `-- Dockerfile
|   |-- Docs/
|   `-- docker-compose.yml
|-- jenkins/
`-- Jenkinsfile
```

## Rutas principales

| Ruta | Vista |
| --- | --- |
| `/` | Redirecciona a `/landing` |
| `/landing` | Landing page |
| `/login` | Inicio de sesion |
| `/signup` | Redirecciona a `/login?panel=register` |
| `/forgot-password` | Recuperar contrasena |
| `/verify-code` | Verificar codigo |
| `/change-password` | Cambiar contrasena |
| `/terms` | Terminos y condiciones |
| `/dashboard` | Dashboard de analisis |
| `/all-environments` | Ambientes |
| `/all-environments?environment=:id` | Ambientes con modal abierto |
| `/ranking` | Ranking ambiental |
| `/environment/:id` | Detalle completo de ambiente |
| `/favorites` | Favoritos |
| `/management` | Gestion de ambientes |
| `/profile` | Perfil |
| `/settings` | Configuracion |

## Ejecucion local

### Frontend

```bash
cd Web/Front-End
npm install
npm run dev
```

Por defecto Vite queda en:

```text
http://localhost:5173
```

Scripts disponibles:

```bash
npm run dev
npm run build
npm run preview
npm run lint
```

### Backend

El backend usa PostgreSQL. La configuracion por defecto espera una base de datos disponible en `postgres:5432`, pensada para Docker Compose.

Para correrlo localmente sin Docker, ajusta `spring.datasource.url` o define variables de entorno equivalentes.

```bash
cd Web/backend
./mvnw spring-boot:run
```

En Windows:

```bash
cd Web/backend
mvnw.cmd spring-boot:run
```

Backend por defecto:

```text
http://localhost:8080
```

Swagger/OpenAPI, si esta habilitado por Springdoc:

```text
http://localhost:8080/swagger-ui.html
```

## Docker Compose

Desde `Web/`:

```bash
docker compose up --build
```

Servicios principales:

| Servicio | Puerto |
| --- | --- |
| Frontend | `3000` |
| Backend | `8080` |
| PostgreSQL | `5432` |
| Jenkins | `8081` |

Frontend en Docker:

```text
http://localhost:3000
```

Backend en Docker:

```text
http://localhost:8080
```

## Variables de entorno

Docker Compose define valores por defecto, pero estas variables se pueden configurar:

| Variable | Uso |
| --- | --- |
| `JWT_SECRET` | Firma de tokens JWT (obligatorio, min 32 caracteres) |
| `JWT_EXPIRATION` | Duracion del token |
| `CORS_ALLOWED_ORIGINS` | Origenes permitidos por CORS |
| `POSTGRES_DB` | Nombre de la base de datos |
| `POSTGRES_USER` | Usuario de PostgreSQL (obligatorio) |
| `POSTGRES_PASSWORD` | Contrasena de PostgreSQL (obligatorio) |
| `POSTGRES_URL` | JDBC URL del backend (opcional; por defecto `postgres:5432`) |
| `SMTP_HOST` | Servidor SMTP para recuperacion de contrasena |
| `SMTP_PORT` | Puerto SMTP |
| `SMTP_USERNAME` | Usuario SMTP |
| `SMTP_PASSWORD` | Contrasena SMTP |
| `MAIL_FROM` | Remitente de los correos |

Copia `Web/.env.example` a `Web/.env` y completa los valores. Docker Compose y el backend leen ese archivo.

Ejemplo:

```bash
JWT_SECRET=una_clave_segura_de_mas_de_32_caracteres
CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:3000
POSTGRES_USER=eduair_user
POSTGRES_PASSWORD=cambia-esta-contrasena
POSTGRES_URL=jdbc:postgresql://localhost:5432/eduaircontrol
SMTP_HOST=sandbox.smtp.mailtrap.io
SMTP_PORT=587
SMTP_USERNAME=...
SMTP_PASSWORD=...
MAIL_FROM=no-reply@eduaircontrol.com
```

Para Liquibase exporta las variables antes de ejecutar:

```bash
set -a; source Web/.env; set +a
```

## Funcionalidades actuales

- Landing responsive con navegacion por secciones.
- Autenticacion con login, registro, recuperacion y cambio de contrasena (SMTP).
- Listado de ambientes con filtros.
- Modal de ambiente desde tarjetas y desde ranking.
- Detalle completo de ambiente.
- Ranking de ambientes por indice ambiental.
- Gestion de ambientes: agregar, editar y eliminar.
- Favoritos compartidos entre ambientes, detalle y favoritos.
- Panel de notificaciones.
- Perfil y configuracion.
- Soporte multiidioma mediante i18n.
- Tema oscuro y tokens visuales compartidos.

## Calidad y verificacion

Comandos recomendados antes de entregar cambios:

```bash
cd Web/Front-End
npm run build
npm run lint
```

Para backend:

```bash
cd Web/backend
mvnw.cmd test
```

## Notas de desarrollo

- El estado de ambientes y favoritos se centraliza en `EnvironmentContext`.
- Las pantallas principales estan organizadas por dominio dentro de `src/modules`.
- La ruta inicial de usuario es `/landing`.
- La navbar interna usa `/all-environments`, `/ranking`, `/favorites`, `/management`, `/profile` y `/settings`.
- El boton "Ver ambiente" en ranking abre el modal en `/all-environments?environment=:id`.
