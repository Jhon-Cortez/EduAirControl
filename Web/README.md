# EduAirControl — Web

Monolito Spring Boot + React que habla contra una sola API (`/api/v1`).
No hay datos de prueba en el frontend: todo viene del backend.

## Rutas rápidas

| Componente | Ruta | URL |
| --- | --- | --- |
| Frontend | `Web/Front-End` | http://localhost:5173 (dev) · http://localhost:3000 (docker) |
| Backend | `Web/backend` | http://localhost:8080 |
| API docs | Swagger | http://localhost:8080/swagger-ui.html |
| Mail local | Mailpit | http://localhost:8025 |
| Compose | `Web/docker-compose.yml` | — |

## Arranque con Docker (recomendado)

```bash
cd EduAirControl/Web
docker compose up --build
```

Levanta: PostgreSQL (con Liquibase y semillas `dev`: 8 ambientes, 32 sensores,
un año de mediciones), backend con el primer administrador, Mailpit y el
frontend compilado. Comprueba el estado en http://localhost:3000/health
(frontend) o `curl http://localhost:8080/health`.

Variables en `Web/.env` (ver `.env.example`):

- `ADMIN_EMAIL` / `ADMIN_PASSWORD` / `ADMIN_COMPANY_CODE` — primer usuario
  ADMIN. `ADMIN_COMPANY_CODE` debe cumplir `AAA-0000` (p. ej. `EDU-0001`).
- `DB_CHANGELOG_CONTEXTS` — `dev` siembra datos de ejemplo; déjalo vacío para
  un arranque solo con esquema + seeds de referencia.
- `SMTP_*` — por defecto apunta al Mailpit local; cámbialo para producción.

## Arranque en local (desarrollo)

```bash
# 1. Base de datos (o usa el Postgres de docker compose)
docker compose up postgres mailpit

# 2. Backend
cd EduAirControl/Web/backend
POSTGRES_URL=jdbc:postgresql://localhost:5432/eduaircontrol \
POSTGRES_USER=eduair_user POSTGRES_PASSWORD=eduair_pass \
JWT_SECRET=<clave-de-32-caracteres-o-mas> \
DB_CHANGELOG_CONTEXTS=dev \
ADMIN_EMAIL=admin@eduaircontrol.com ADMIN_PASSWORD=Admin1234! \
ADMIN_COMPANY_CODE=EDU-0001 \
mvn spring-boot:run

# 3. Frontend
cd EduAirControl/Web/Front-End
npm install
npm run dev      # http://localhost:5173
```

`Front-End/.env` solo necesita `VITE_API_URL` (por defecto
`http://localhost:8080`).

## Alta de un dispositivo ESP32 (ingesta IoT)

Los dispositivos no se siembran: se dan de alta desde la API y la clave se
devuelve **una sola vez**.

```bash
# 1. Login del administrador
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@eduaircontrol.com","password":"Admin1234!","companyCode":"EDU-0001"}' \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

# 2. Alta del dispositivo (la apiKey solo se muestra aquí)
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"macAddress":"AA:BB:CC:DD:EE:FF","name":"ESP32 Aula 209-1",
       "educationalEnvironmentId":"<uuid-del-ambiente>"}'

# 3. Enviar lecturas (autenticado con X-API-Key, sin JWT)
curl -X POST http://localhost:8080/ingest/v1/measurements \
  -H "X-API-Key: ea_..." -H 'Content-Type: application/json' \
  -d '{"serial":"EA-209-1-T","measuredAt":"2026-10-01T12:00:00Z",
       "readings":[{"variable":"temperature","value":21.5}]}'
```

Si pierdes la clave: `POST /api/v1/devices/{macAddress}/rotate-key` genera una
nueva y anula la anterior (también se muestra una sola vez).

## Comandos de verificación

```bash
cd EduAirControl/Web/Front-End && npm run lint && npm run build
cd EduAirControl/Web/backend  && mvn test        # Testcontainers PostgreSQL
```

## Contrato de errores

Todo error responde JSON con la forma `{"status": <código>,
"message": "<texto>"}`; 401 sin sesión, 403 sin rol ADMIN en escrituras de
catálogo, 409 conflicto de unicidad.
