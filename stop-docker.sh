#!/bin/bash

echo "Deteniendo contenedores..."
docker-compose down

echo "Estado actual:"
docker-compose ps

echo "Contenedores detenidos"