#!/usr/bin/env bash
# Despliega NavegaSmart en AWS (o publica una versión nueva si ya está desplegado).
#   1. Construye las imágenes Docker y las sube a ECR.
#   2. Crea o actualiza la pila de CloudFormation (EC2 + IP elástica + CloudFront).
#   3. Por SSM, copia docker-compose.yml a la instancia y arranca los contenedores.
# Requisitos: AWS CLI v2 con credenciales, Docker en ejecución y Python 3.
# Uso: bash deploy/aws/deploy.sh            (variables opcionales: AWS_REGION, STACK_NAME, INSTANCE_TYPE)
set -euo pipefail
export MSYS_NO_PATHCONV=1   # Git Bash en Windows: no convertir argumentos que parecen rutas

REGION="${AWS_REGION:-us-east-1}"
STACK="${STACK_NAME:-navegasmart}"
INSTANCE_TYPE="${INSTANCE_TYPE:-t3.micro}"
AZ="${AVAILABILITY_ZONE:-${REGION}a}"

# En Git Bash, `pwd -W` da rutas C:/... que entienden docker.exe y aws.exe; en Linux/macOS se usa `pwd`.
abspath() { (cd "$1" && { pwd -W 2>/dev/null || pwd; }); }
HERE="$(abspath "$(dirname "${BASH_SOURCE[0]}")")"
ROOT="$(abspath "$HERE/../..")"
PYTHON="$(command -v python3 || command -v python)"
export AWS_REGION="$REGION" AWS_PAGER=""

step() { printf '\n==> %s\n' "$*"; }

ACCOUNT="$(aws sts get-caller-identity --query Account --output text)"
REGISTRY="$ACCOUNT.dkr.ecr.$REGION.amazonaws.com"
TAG="$(date +%Y%m%d-%H%M%S)"
BACKEND_IMAGE="$REGISTRY/navegasmart-backend:$TAG"
FRONTEND_IMAGE="$REGISTRY/navegasmart-frontend:$TAG"

step "Cuenta $ACCOUNT, región $REGION, versión $TAG"

step "Repositorios ECR"
for repo in navegasmart-backend navegasmart-frontend; do
  if ! aws ecr describe-repositories --repository-names "$repo" >/dev/null 2>&1; then
    aws ecr create-repository --repository-name "$repo" \
      --image-scanning-configuration scanOnPush=true >/dev/null
    echo "creado $repo"
  fi
  # Conserva solo las 5 imágenes más recientes para no acumular costo de almacenamiento.
  aws ecr put-lifecycle-policy --repository-name "$repo" --lifecycle-policy-text \
    '{"rules":[{"rulePriority":1,"description":"Conservar 5","selection":{"tagStatus":"any","countType":"imageCountMoreThan","countNumber":5},"action":{"type":"expire"}}]}' >/dev/null
done
aws ecr get-login-password | docker login --username AWS --password-stdin "$REGISTRY" >/dev/null

step "Construyendo y subiendo imágenes"
docker build --platform linux/amd64 -t "$BACKEND_IMAGE" "$ROOT/backend"
docker build --platform linux/amd64 -t "$FRONTEND_IMAGE" "$ROOT/frontend"
docker push -q "$BACKEND_IMAGE"
docker push -q "$FRONTEND_IMAGE"

step "Pila de CloudFormation $STACK (la primera vez tarda ~5-10 min por CloudFront)"
VPC_ID="$(aws ec2 describe-vpcs --filters Name=isDefault,Values=true --query 'Vpcs[0].VpcId' --output text)"
SUBNET_ID="$(aws ec2 describe-subnets --filters Name=vpc-id,Values="$VPC_ID" Name=availability-zone,Values="$AZ" Name=default-for-az,Values=true --query 'Subnets[0].SubnetId' --output text)"
PREFIX_LIST="$(aws ec2 describe-managed-prefix-lists --filters Name=prefix-list-name,Values=com.amazonaws.global.cloudfront.origin-facing --query 'PrefixLists[0].PrefixListId' --output text)"
aws cloudformation deploy \
  --stack-name "$STACK" \
  --template-file "$HERE/template.yaml" \
  --capabilities CAPABILITY_IAM \
  --no-fail-on-empty-changeset \
  --parameter-overrides InstanceType="$INSTANCE_TYPE" VpcId="$VPC_ID" SubnetId="$SUBNET_ID" CloudFrontPrefixListId="$PREFIX_LIST"

output() { aws cloudformation describe-stacks --stack-name "$STACK" --query "Stacks[0].Outputs[?OutputKey=='$1'].OutputValue" --output text; }
INSTANCE_ID="$(output InstanceId)"
URL="$(output Url)"

step "Esperando a que la instancia $INSTANCE_ID esté disponible en SSM"
for _ in $(seq 1 60); do
  status="$(aws ssm describe-instance-information --filters Key=InstanceIds,Values="$INSTANCE_ID" --query 'InstanceInformationList[0].PingStatus' --output text 2>/dev/null || true)"
  [ "$status" = "Online" ] && break
  sleep 10
done
[ "$status" = "Online" ] || { echo "La instancia no se registró en SSM." >&2; exit 1; }

step "Arrancando contenedores en la instancia"
COMPOSE_B64="$(base64 -w0 < "$HERE/docker-compose.yml" 2>/dev/null || base64 < "$HERE/docker-compose.yml" | tr -d '\n')"
REMOTE_SCRIPT="$(cat <<EOF
set -euo pipefail
cloud-init status --wait >/dev/null || true
cd /opt/navegasmart
echo '$COMPOSE_B64' | base64 -d > docker-compose.yml
printf 'BACKEND_IMAGE=%s\nFRONTEND_IMAGE=%s\n' '$BACKEND_IMAGE' '$FRONTEND_IMAGE' > images.env
aws ecr get-login-password --region $REGION | docker login --username AWS --password-stdin $REGISTRY >/dev/null
compose() { docker compose --env-file .env --env-file images.env "\$@"; }
compose pull --quiet
compose up -d --remove-orphans
docker image prune -f >/dev/null
for i in \$(seq 1 60); do
  code=\$(curl -s -o /dev/null -w '%{http_code}' http://localhost/api/strategies || true)
  [ "\$code" = 401 ] && { echo "Backend listo"; break; }
  sleep 5
done
compose ps
EOF
)"
PARAMS="$("$PYTHON" -c 'import base64,json,sys; s=base64.b64encode(sys.argv[1].encode()).decode(); print(json.dumps({"commands":["echo %s | base64 -d > /tmp/navegasmart-deploy.sh" % s, "bash /tmp/navegasmart-deploy.sh"],"executionTimeout":["1800"]}))' "$REMOTE_SCRIPT")"
COMMAND_ID="$(aws ssm send-command --instance-ids "$INSTANCE_ID" --document-name AWS-RunShellScript \
  --comment "NavegaSmart $TAG" --parameters "$PARAMS" --query Command.CommandId --output text)"

while true; do
  state="$(aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" --query Status --output text 2>/dev/null || echo Pending)"
  case "$state" in Pending|InProgress|Delayed) sleep 10 ;; *) break ;; esac
done
aws ssm get-command-invocation --command-id "$COMMAND_ID" --instance-id "$INSTANCE_ID" \
  --query '[StandardOutputContent,StandardErrorContent]' --output text | tail -n 25
[ "$state" = "Success" ] || { echo "El despliegue en la instancia terminó con estado $state." >&2; exit 1; }

step "Listo: $URL"
