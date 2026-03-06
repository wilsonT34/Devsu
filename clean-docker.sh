#!/bin/bash

echo "ATENCIÓN: Esto eliminará TODOS los contenedores, volúmenes e imágenes"
read -p "¿Continuar? (s/N): " -n 1 -r
echo

if [[ $REPLY =~ ^[Ss]$ ]]
then
    echo "Deteniendo contenedores..."
    docker-compose down -v
    
    echo "Eliminando imágenes..."
    docker rmi $(docker images | grep "cliente-persona-ms" | awk '{print $3}') 2>/dev/null
    docker rmi $(docker images | grep "cuenta-movimiento-ms" | awk '{print $3}') 2>/dev/null
    
    echo "🧹 Limpiando volúmenes huérfanos..."
    docker volume prune -f
    
    echo "Limpieza completada"
else
    echo "Operación cancelada"
fi