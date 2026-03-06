# Devsu
Solución Microservicios
### **Microservicios:**

1. **cliente-persona-ms** (Puerto: 8081)
   ## Gestión de clientes y personas
   ## CRUD completo de clientes
   ## Publica eventos cuando se crean/actualizan/eliminan clientes

2. **cuenta-movimiento-ms** (Puerto: 8082)
   ## Gestión de cuentas bancarias
   ## Registro de movimientos (depósitos/retiros)
   ## Validación de saldo disponible
   ## Generación de reportes de estado de cuenta
   ## Consume eventos de clientes vía RabbitMQ

## Tecnologías

## Java 17** (Zulu JDK)
## Spring Boot 3.1.5**
## Spring Data JPA** ## Persistencia
## Spring AMQP** ## Comunicación asíncrona
## PostgreSQL 15** ## Base de datos
## RabbitMQ 3.12** ## Message broker
## Maven 3.9.3** ## Gestión de dependencias
## Docker & Docker Compose** ## Contenerización
## Lombok** ## Reducción de código boilerplate
## ModelMapper** ## Mapeo de objetos
## JUnit 5 & Mockito** ## Pruebas unitarias

## Requisitos Previos

## Docker Desktop** (v20.10+)
## Docker Compose** (v2.0+)
## Git** (opcional, para clonar)
## Postman** (para probar los endpoints)

Verifica las instalaciones:
```bash
docker --version
docker-compose --version

## Nos dirigimos a directorio del proyecto
##Script para levantar el proyecto en dockert
# 1 -  docker-compose down -v
# 2 -  docker-compose build --no-cache
# 3 -  docker-compose up -d
# 4 -  docker-compose logs -f 

##Endpoints de la API
##Microservicio Cliente-Persona
## Endpoints desplegados en el puerto 8081

Método	   Endpoint	                                       Descripción
GET	      /api/clientes/test	                           Verificar servicio
GET	      /api/clientes	Listar                           todos los clientes
GET	      /api/clientes/{id}	                           Obtener cliente por ID
POST	      /api/clientes	Crear                            nuevo cliente
PUT	      /api/clientes/{id}	                           Actualizar cliente
PATCH	      /api/clientes/{id}/estado?estado=true/false	   Cambiar estado
DELETE	   /api/clientes/{id}	                           Eliminar cliente

##Microservicio Cuenta-Movimiento
## Endpoints desplegados en el puerto 8082

Cuentas
Método	   Endpoint	                                       Descripción
GET	      /api/cuentas/test	                              Verificar servicio
GET	      /api/cuentas	                                 Listar todas las cuentas
GET	      /api/cuentas/{numero}	                        Obtener cuenta por número
GET	      /api/cuentas/cliente/{clienteId}	               Cuentas por cliente
POST	      /api/cuentas	                                 Crear cuenta
PUT	      /api/cuentas/{numero}	                        Actualizar cuenta
PATCH	      /api/cuentas/{numero}/estado?estado=true/false	Cambiar estado

Movimientos
Método	   Endpoint	                                       Descripción
POST	      /api/movimientos	                              Registrar movimiento
GET	      /api/movimientos/cuenta/{numero}	               Movimientos por cuenta

Reportes
Método	   Endpoint	                                                                           Descripción
GET	      /api/reportes?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD&cliente={id}	            Reporte estado cuenta
GET	      /api/reportes/detallado?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD&cliente={id}	   Reporte detallado


##Script de base de datos:
-- banco Microservices Database Schema

CREATE TABLE IF NOT EXISTS personas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(20),
    edad INTEGER,
    identificacion VARCHAR(20) UNIQUE NOT NULL,
    direccion VARCHAR(200),
    telefono VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS clientes (
    clienteid VARCHAR(20) PRIMARY KEY,
    contrasena VARCHAR(100) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    persona_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (persona_id) REFERENCES personas(id)
);

CREATE TABLE IF NOT EXISTS cuentas (
    numero_cuenta VARCHAR(20) PRIMARY KEY,
    tipo_cuenta VARCHAR(20) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0,
    saldo_disponible DECIMAL(15,2) NOT NULL DEFAULT 0,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    clienteid VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento VARCHAR(20) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL,
    FOREIGN KEY (numero_cuenta) REFERENCES cuentas(numero_cuenta)
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_movimientos_fecha ON movimientos(fecha);
CREATE INDEX IF NOT EXISTS idx_movimientos_cuenta ON movimientos(numero_cuenta);
CREATE INDEX IF NOT EXISTS idx_cuentas_cliente ON cuentas(clienteid);

#Datos para agregar a la base de datos y realizar las pruebas: 
#Se enmcuentran en el archivo: Banking-Microservices.postman_collection.json
#Se debe importar a un Collections de postman.


AUTOR:
- @wilsonT34
