# DenunciasApp – Aplicación de Denuncias Ciudadanas

## Descripción

DenunciasApp es una aplicación móvil desarrollada en Android Studio utilizando Kotlin. Permite registrar, consultar y atender denuncias relacionadas con la inseguridad ciudadana.

La aplicación consume una API REST desarrollada con Spring Boot y conectada a una base de datos MySQL.

## Funcionalidades

- Registrar denuncias ciudadanas.
- Visualizar denuncias mediante RecyclerView.
- Listar denuncias con paginación de cinco registros.
- Cargar más denuncias.
- Buscar denuncias por DNI del ciudadano.
- Consultar los detalles de una denuncia.
- Marcar una denuncia como atendida.
- Visualizar el estado de cada denuncia.

## Tecnologías utilizadas

- Kotlin
- Android Studio
- XML para las interfaces
- Retrofit
- Gson
- RecyclerView
- API REST con Spring Boot
- MySQL

## Estructura del proyecto

- `model`: Modelo de datos de las denuncias.
- `network`: Configuración de Retrofit y conexión con la API.
- `adapter`: Adaptador del RecyclerView.
- `MainActivity.kt`: Pantalla principal y funciones de la aplicación.
- `res/layout`: Interfaces XML.
- `res/mipmap`: Recursos del ícono de la aplicación.

## Requisitos

- Android Studio.
- Dispositivo Android o emulador.
- API REST de denuncias ejecutándose.
- Base de datos MySQL configurada.

## Ejecución

1. Descargar o clonar este repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar las dependencias de Gradle.
4. Iniciar la base de datos MySQL y la API REST en el puerto 8080.
5. Conectar un dispositivo Android mediante USB y habilitar la depuración USB.
6. Ejecutar `adb reverse tcp:8080 tcp:8080` para permitir la conexión con la API local.
7. Ejecutar la aplicación desde Android Studio.

**Nota:** La configuración actual utiliza `http://127.0.0.1:8080/` para acceder a la API mediante un dispositivo conectado por USB con ADB reverse. Si se utiliza un emulador o una API desplegada en otro servidor, se debe adaptar la dirección en `ApiClient.kt`.

## Estados de las denuncias

- **1:** Denuncia pendiente.
- **0:** Denuncia atendida.

## Repositorio de la API REST

https://github.com/Kataper31/denunciaservice

## Finalidad académica

Proyecto desarrollado como parte del Taller de Desarrollo de Aplicaciones Móviles, para implementar una solución de registro y seguimiento de denuncias ciudadanas mediante una aplicación Android y servicios REST.
