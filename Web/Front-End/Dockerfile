# Etapa 1: build
FROM node:20 AS builder

WORKDIR /app

ARG VITE_API_URL=http://localhost:8080
ARG VITE_DB_URL=http://localhost:3001
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_DB_URL=$VITE_DB_URL

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Etapa 2: servidor
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
