# 🏥 API MiSalud Familiar

**API REST** desarrollada en **Java y Spring Boot** diseñada para resolver la fragmentación, el olvido y la falta de seguimiento preventivo en el cuidado de la salud dentro del núcleo familiar.

En la mayoría de los hogares, los datos clínicos se pierden entre recetas físicas, estudios sueltos y medicamentos sin control de caducidad. Este backend provee la lógica de negocio, seguridad y persistencia para atacar **6 problemas clave del día a día**:

- **Adherencia a tratamientos:** Genera automáticamente calendarios de tomas programadas a partir de cada receta médica y registra su cumplimiento en tiempo real.
- **Control del botiquín en casa:** Administra el stock de medicamentos por familia, calculando existencias y alertando sobre insumos bajo mínimo o próximos a caducar.
- **Expediente clínico centralizado:** Consolida consultas, diagnósticos, notas clínicas, alergias por severidad, directorio de médicos y resultados de laboratorio.
- **Seguimiento de vacunación:** Cruza la edad de cada integrante con los esquemas oficiales para calcular automáticamente dosis aplicadas, pendientes o atrasadas.
- **Coordinación entre cuidadores:** Separa responsabilidades dentro del mismo hogar mediante roles (`ADMIN` y `CUIDADOR`) con aislamiento total de datos por familia.
- **Prevención mediante alertas:** Consolida KPIs en tiempo real y notificaciones automáticas antes de que ocurran urgencias o faltantes de medicación.

### 🔗 Enlaces del Proyecto
- **Cliente Web (Repositorio):** [misalud-familiar-app-web](https://github.com/AlfreGood20/misalud-familiar-app-web)
- **Aplicación Web en Vivo (Vercel):** [https://misalud-familiar.vercel.app](https://misalud-familiar.vercel.app)
- **API en Producción (Render):** [https://api-gestor-familiar.onrender.com](https://api-gestor-familiar.onrender.com)
- **Documentación Interactiva (Swagger UI):** [https://api-gestor-familiar.onrender.com/swagger](https://api-gestor-familiar.onrender.com/swagger)

## 🛠️ Stack Tecnológico

### Backend & Base de Datos
[![Stack Backend](https://skillicons.dev/icons?i=java,spring,postgres,supabase,maven)](https://skillicons.dev)

- **Lenguaje:** Java
- **Framework:** Spring Boot (Spring Web, Spring Data JPA, Spring Security + Session, Bean Validation)
- **Base de Datos:** PostgreSQL alojado en Supabase
- **Documentación API:** Springdoc OpenAPI / Swagger UI
- **Construcción y Dependencias:** Maven (`mvnw`)

### Herramientas de Desarrollo
[![Herramientas](https://skillicons.dev/icons?i=git,github,idea,postman)](https://skillicons.dev)

## 🏗️ Arquitectura y Estructura del Repositorio

El proyecto sigue una **arquitectura modular orientada por dominio (*Package by Feature*)**, donde cada paquete agrupa su controlador (`Control`), servicio (`Serv`), repositorio (`Repo`), entidad, enumeraciones y objetos de transferencia (`dto`)[cite: 2, 7, 14].

```text
api-gestor-familiar-medico/
├── src/main/java/io/github/alfregood20/apigestorfamiliarmedico/
│   ├── alergia/        # Catálogo global de alergias y asignación por integrante
│   ├── auth/           # Registro, inicio de sesión (JWT) y perfil activo
│   ├── configuration/  # Seguridad (Spring Security), CORS y Swagger/OpenAPI
│   ├── consultas/      # Agenda de citas médicas, diagnósticos e historial
│   ├── dashboard/      # Indicadores consolidados (KPIs) y resumen del hogar
│   ├── estudio/        # Estudios de laboratorio, categorías y carga de resultados
│   ├── exception/      # Manejo global de excepciones y respuestas de error
│   ├── familia/        # Gestión del núcleo familiar y aislamiento de datos
│   ├── integrante/     # Expedientes clínicos de cada miembro del hogar
│   ├── medicamento/    # Catálogo base de fármacos
│   ├── medico/         # Directorio médico familiar y especialidades
│   ├── receta/         # Recetas médicas, detalles y agenda de tomas programadas
│   ├── stock/          # Inventario del botiquín, caducidades y alertas de mínimo
│   ├── usuario/        # Administración de cuentas y roles (ADMIN / CUIDADOR)
│   └── vacunas/        # Catálogo oficial, esquemas por edad y cartilla aplicada
├── diagrama_ER.md                  # Modelo Entidad-Relación de la base de datos
├── spring_security.md              # Documentación de flujos de autenticación y roles
├── tablas_proyecto_taller_db.sql   # Script DDL completo para PostgreSQL
└── pom.xml                         # Dependencias y configuración de Maven
```

## 📋 Requisitos Previos

Antes de ejecutar el proyecto en tu entorno local, asegúrate de contar con:

- **Java Development Kit (JDK):** Versión **17** (LTS).
- **Gestor de dependencias:** Apache Maven 3.8+ *(o utilizar el wrapper `./mvnw` incluido en el repositorio)*.
- **Base de Datos:** PostgreSQL 14+ (local o en la nube mediante **Supabase**).
- **Control de versiones:** Git.

## ⚙️ Variables de Entorno y Configuración

El proyecto está configurado mediante variables de entorno en `src/main/resources/application.properties` para evitar exponer credenciales sensibles en el repositorio.

### 1. Variables requeridas
Antes de ejecutar la aplicación, define las siguientes variables de entorno en tu sistema operativo, IDE (IntelliJ IDEA) o plataforma de despliegue (Render):

| Variable | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `DB_URL` | Cadena de conexión JDBC hacia PostgreSQL / Supabase | `jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres.tu_id_proyecto` |
| `DB_PASSWORD` | Contraseña de la base de datos | `tu_contraseña_segura` |
| `PORT` | Puerto HTTP donde escucha el servidor *(opcional, por defecto `8080`)* | `8080` |

### 2. Archivo `application.properties` de referencia
```properties
spring.application.name=api-gestor-familiar-medico

# Conexión a Base de Datos (PostgreSQL / Supabase)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Configuración de Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=false

# Configuración del Servidor y Cookies de Sesión
server.port=${PORT:8080}
server.servlet.session.cookie.same-site=none
server.servlet.session.cookie.secure=true
server.forward-headers-strategy=framework

# Documentación Interactiva (Swagger / OpenAPI)
springdoc.swagger-ui.path=/swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.operations-sorter=alpha
```
## 🔐 Seguridad, Autenticación y Roles

La API utiliza **Spring Security con manejo de sesiones HTTP (`HttpSession`) mediante cookies seguras (`JSESSIONID`)**, configuradas para entornos cross-origin (`SameSite=None; Secure=true`).

### Flujo de Autenticación
1. **Registro (`POST /auth/registro`):** Crea el núcleo familiar, el usuario principal con rol `ADMIN` y su expediente como integrante `TITULAR`.
2. **Inicio de Sesión (`POST /auth/login`):** Valida credenciales, regenera el ID de sesión para prevenir *Session Fixation* y establece la cookie de sesión en el navegador.
3. **Peticiones Protegidas:** El cliente web debe enviar las credenciales de sesión (`credentials: 'include'` / `withCredentials: true`) en cada solicitud.
4. **Cierre de Sesión (`POST /auth/logout`):** Invalida la sesión activa y limpia el contexto de seguridad.

### Matriz de Permisos por Rol y Aislamiento Familiar
Todos los recursos clínicos y operativos están aislados por el `id_familia` del usuario autenticado.

| Rol | Alcance y Permisos |
| :--- | :--- |
| **`ADMIN`** | Control total del hogar: administración de cuidadores (`/usuarios/**`), creación/edición/eliminación de integrantes (`/integrantes/**`), médicos, y borrado físico de registros clínicos, recetas, alertas y stock. |
| **`CUIDADOR`** | Operación clínica y diaria: lectura de integrantes, médicos y dashboard; registro y seguimiento de consultas, estudios, vacunas aplicadas, recetas, tomas programadas (`/tomas/**`), alertas (`/alertas/**`) y gestión operativa del botiquín (`/stock/**`) sin permisos de eliminación. |
| **Catálogos Maestros** | Endpoints de solo lectura (`GET`) para usuarios autenticados sobre `/vacunas`, `/esquemas-vacunacion`, `/alergias`, `/especialidades` y `/medicamentos`. |

---

## 📡 Resumen de Módulos y Endpoints Principales

Todos los endpoints protegidos requieren una sesión activa (`JSESSIONID`) y aíslan automáticamente los datos por el núcleo familiar del usuario autenticado.

| Módulo | Ruta Base | Operaciones Principales |
| :--- | :--- | :--- |
| **Autenticación y Perfil** | `/auth` | Registro de hogar/admin (`POST /registro`), login con cookie (`POST /login`), logout (`POST /logout`), perfil actual (`GET /me`, `PUT /me`) y cambio de contraseña (`PATCH /me/password`) |
| **Dashboard** | `/dashboard` | Indicadores clave (KPIs), tomas del día, próximas citas y resumen de integrantes (`GET`) |
| **Usuarios / Cuidadores** | `/usuarios` | Administración de cuentas de cuidadores (`GET`, `POST`, `PUT`), detalle y activación/desactivación (`PATCH`) |
| **Integrantes** | `/integrantes` | CRUD de expedientes clínicos, consultas por integrante (`GET /{id}/consultas`) y cartilla de vacunación (`GET /{id}/cartilla-vacunacion`)|
| **Alergias** | `/alergias` | Catálogo global de alergias y asignación por severidad a cada integrante |
| **Medicamentos** | `/medicamentos` | Catálogo base de fármacos registrados en el sistema |
| **Botiquín (Stock)** | `/stock` | Inventario familiar con filtros, ajustes rápidos (`PATCH /{id}`), alertas de bajo mínimo y caducidades próximas |
| **Directorio Médico** | `/medicos` | Registro y consulta de médicos y especialidades de la familia[cite: 13] |
| **Consultas Médicas** | `/consultas` | Agenda de citas médicas, registro de diagnósticos, notas clínicas y seguimiento|
| **Recetas y Tomas** | `/recetas`, `/tomas` | Creación de recetas con generación automática de cronogramas y control de dosis (`GET /tomas`, `PATCH /tomas/{id}/estado`)|
| **Estudios de Laboratorio** | `/estudios-laboratorio` | Registro de análisis clínicos, vinculación con consultas y carga de resultados|
| **Vacunación** | `/vacunas`, `/esquemas-vacunacion`, `/aplicaciones-vacunas` | Catálogo oficial, esquemas por edad y registro/edición de dosis aplicadas |
| **Alertas** | `/alertas` | Notificaciones preventivas del hogar (stock bajo, caducidad, vacunas, tomas, bienvenida) y marcado de lectura (`PATCH`) |

## 👨‍💻 Autor

**José Alfredo** ([@AlfreGood20](https://github.com/AlfreGood20))

- **GitHub:** [https://github.com/AlfreGood20](https://github.com/AlfreGood20)
- **Repositorio Backend (API):** [api-gestor-familiar-medico](https://github.com/AlfreGood20/api-gestor-familiar-medico)
- **Repositorio Frontend (Web):** [web-gestor-familiar-medico](https://github.com/AlfreGood20/web-gestor-familiar-medico)

---
* Inicio del proyecto: **11 de octubre de 2026**

* Ultima actulización: **27 de noviembre de 2026**
---

## 📄 Licencia

Este proyecto fue desarrollado con fines **académicos y de portafolio** bajo la **Licencia MIT**. Consulta el archivo `LICENSE` (si aplica) o utilízalo libremente como referencia educativa dando el crédito correspondiente al autor.
