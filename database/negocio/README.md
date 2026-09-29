# Modelo de datos de negocio (núcleo, 3FN)

Este módulo versiona con Liquibase el modelo de negocio de EduAirControl, curado a partir de
`code-sena/ea-control-docs` → `06-data/domain/*.md` (fuente canónica, corte 2026-09-24).

## Esquemas (1 por dominio — ADR-003)

| Esquema | Bounded context | Microservicio(s) | Tablas |
|---------|-----------------|------------------|--------|
| `classrooms` | Classrooms | `ms-classroom-management` | `campuses`, `environment_types`, `educational_environment` |
| `sensors` | Sensors | `ms-sensor-management` | `sensor`, `sensor_installation`, `variable`, `sensor_variable` |
| `monitoring` | Monitoring + Data Analysis | `ms-environment-monitoring` (alertas: ver nota) | `severity`, `variable_threshold`, `environment_measurement`, `environmental_analysis`, `analysis_result`, `environment_alert` |
| `ux` | UX | `ms-user-experience` | `user_preferences`, `favorites`, `classroom_ratings`, `searches` |

**Total: 17 tablas de negocio.**

Nota sobre alertas: `environment_alert` vive en `monitoring` porque se generan directamente del
flujo de medición (decisión de implementación; en `09-microservices/service-catalog.md` la
propiedad de despliegue es `ms-alert`).

## Reglas del modelo

- **Nombres**: tablas en inglés y en singular (`educational_environment`, `sensor`,
  `variable_threshold`…) — `00-governance/documentation-rules.md`. Excepción: las tablas UX
  mantienen plural (`favorites`, `searches`) por la exención de gobernanza (open item #10).
- **FKs**: física **dentro** de cada dominio (9 constraint en `01_ddl/04_alter`); entre dominios,
  UUID lógico sin FK (patrón de `06-data/domain/*` + ADR-003: cross-service solo API/events).
- **CREATE TABLE sin FKs**: las FKs se agregan después en `04_alter` (guía del proyecto,
  igual que `identidad-seguridad`).

## Exclusiones deliberadas

1. **IAM (13 tablas de `ms-iam.md`)** — vive en el esquema `seguridad`
   (`database/identidad-seguridad/`). Nada de negocio depende de su esquema interno, solo de
   UUIDs de usuario tratados aquí como referencia lógica (`user_id`, `requested_by`,
   `acknowledged_by`).
2. **Catálogos de parametrización (4)** — `sensor_models`, `sensor_statuses`,
   `measurement_units`, `alert_status`: se conservan solo como columna UUID sin tabla ni FK
   (`sensor_model_id`, `sensor_status_id`, `measurement_unit_id`, `alert_status_id`).
   Diferencia con la fuente canónica: los docs los tienen como tablas (21 tablas de negocio);
   aquí son 17. En una partición por microservicios da igual si viven en un servicio de
   catálogos o como tabla de referencia dentro de cada dominio.
3. **`severity` sí es tabla** (lookup con `code`, `name`, `level`) — restaurada según el
   open item #5 del assessment; es parte de la regla de umbrales, no parametrización pura.

## Tablas de la fuente sin implementación aquí

`quality_flag_id` y `analysis_status_id` son columnas lógicas sin tabla en el modelo canónico
(marcas de calidad/estado de proceso, validadas por la aplicación).

## Aplicación local

Desde esta carpeta, con PostgreSQL disponible en el host:

```powershell
liquibase --defaultsFile=liquibase.properties update
```

Plan sin cambios:

```powershell
liquibase --defaultsFile=liquibase.properties updateSQL
```

Validación rápida sin CLI (aplicar los SQL en orden: schemas → tables → alter → indexes):

```bash
createdb eduaircontrol_test
# desde changelog/:
psql -d eduaircontrol_test -f 01_ddl/01_schemas/001_schemas.sql
for f in $(find 01_ddl/03_tables -name '*.sql' | sort); do psql -v ON_ERROR_STOP=1 -d eduaircontrol_test -f "$f"; done
psql -v ON_ERROR_STOP=1 -d eduaircontrol_test -f 01_ddl/04_alter/001_foreign_keys.sql
psql -v ON_ERROR_STOP=1 -d eduaircontrol_test -f 01_ddl/10_indexes/001_indexes.sql
```

Esperado: 4 esquemas, 17 tablas, 9 FKs, 23 índices.
