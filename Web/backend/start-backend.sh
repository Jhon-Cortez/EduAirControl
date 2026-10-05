#!/bin/bash

# Cargar variables de entorno desde el archivo .env (un nivel arriba)
ENV_FILE="../.env"
if [ -f "$ENV_FILE" ]; then
    export $(grep -v '^#' "$ENV_FILE" | xargs)
fi

# Iniciar el backend con todas las variables de entorno
./mvnw spring-boot:run
