#!/bin/bash

echo "========================================="
echo " BANCO MICROSERVICES - DOCKER DEPLOY"
echo "========================================="

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar Docker
echo -e "${YELLOW} Verificando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED} Docker no está instalado${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED} Docker Compose no está instalado${NC}"
    exit 1
fi

echo -e "${GREEN} Docker y Docker Compose están instalados${NC}"

# Construir y ejecutar
echo -e "${YELLOW}  Construyendo imágenes y levantando contenedores...${NC}"
docker-compose up -d --build

# Esperar a que los servicios estén listos
echo -e "${YELLOW} Esperando a que los servicios estén listos...${NC}"
sleep 15

# Verificar estado
echo -e "\n${YELLOW} Estado de los contenedores:${NC}"
docker-compose ps

# Mostrar URLs
echo -e "\n${GREEN}=========================================${NC}"
echo -e "${GREEN} SISTEMA DESPLEGADO CORRECTAMENTE${NC}"
echo -e "${GREEN}=========================================${NC}"
echo -e "PostgreSQL:      ${YELLOW}localhost:5432${NC}"
echo -e "RabbitMQ Admin:  ${YELLOW}http://localhost:15672${NC} (admin/admin123)"
echo -e "Cliente-Persona: ${YELLOW}http://localhost:8081/api/clientes/test${NC}"
echo -e "Cuenta-Movimiento: ${YELLOW}http://localhost:8082/api/cuentas/test${NC}"
echo -e "Reportes:        ${YELLOW}http://localhost:8082/api/reportes?fechaInicio=2026-03-01&fechaFin=2026-03-04&cliente=1${NC}"
echo -e "${GREEN}=========================================${NC}"

# Verificar logs
echo -e "\n${YELLOW} Últimos logs de los microservicios:${NC}"
docker-compose logs --tail=10 cliente-persona-ms
docker-compose logs --tail=10 cuenta-movimiento-ms

echo -e "\n${YELLOW} Para ver logs en tiempo real:${NC}"
echo "  docker-compose logs -f"
echo "  docker-compose logs -f cliente-persona-ms"
echo "  docker-compose logs -f cuenta-movimiento-ms"