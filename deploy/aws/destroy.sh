#!/usr/bin/env bash
# Elimina todo lo que creó deploy.sh: la pila (EC2, IP elástica, CloudFront, rol, grupo de seguridad)
# y los repositorios de ECR con sus imágenes. La base de datos de la instancia se pierde.
# Uso: bash deploy/aws/destroy.sh
set -euo pipefail
export MSYS_NO_PATHCONV=1

REGION="${AWS_REGION:-us-east-1}"
STACK="${STACK_NAME:-navegasmart}"
export AWS_REGION="$REGION" AWS_PAGER=""

read -r -p "Se eliminará la pila '$STACK' y sus datos en $REGION. Escribe 'eliminar' para continuar: " answer
[ "$answer" = "eliminar" ] || { echo "Cancelado."; exit 1; }

echo "Eliminando la pila (CloudFront tarda ~10-15 min en borrarse)..."
aws cloudformation delete-stack --stack-name "$STACK"
aws cloudformation wait stack-delete-complete --stack-name "$STACK"

for repo in navegasmart-backend navegasmart-frontend; do
  aws ecr delete-repository --repository-name "$repo" --force >/dev/null 2>&1 && echo "Eliminado $repo" || true
done
echo "Listo: no quedan recursos de NavegaSmart en $REGION."
