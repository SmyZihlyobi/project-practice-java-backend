#!/bin/bash

set -e
set -o pipefail

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &> /dev/null && pwd)
ENV_FILE="${SCRIPT_DIR}/.env"

if [ -f "$ENV_FILE" ]; then
  echo "Загрузка переменных окружения из ${ENV_FILE}"
  # export .env
  set -a
  source "$ENV_FILE"
  set +a
else
  echo "ПРЕДУПРЕЖДЕНИЕ: Файл .env не найден в директории скрипта (${SCRIPT_DIR})."
  echo "             Попытка использовать переменные, уже установленные в окружении."
fi


PG_CONTAINER_NAME="${PG_CONTAINER_NAME:-project-practice-java-backend-postgres-1}"
PG_USER="${POSTGRES_USER}"
PG_DB="${POSTGRES_DB}"
PG_PASSWORD="${POSTGRES_PASSWORD}"

MINIO_VOLUME_NAME="${MINIO_VOLUME_NAME:-project-practice-java-backend_minio_data}" # Имя из вашего docker-compose для MinIO

BACKUP_DIR="${BACKUP_DIR:-./backups}"

if [ -z "$PG_CONTAINER_NAME" ] || [[ "$PG_CONTAINER_NAME" == *"НАДО_УКАЗАТЬ"* ]]; then # Добавил проверку на случай, если значение по умолчанию не заменилось
  echo "ОШИБКА: Не удалось определить имя PostgreSQL контейнера. Укажите его в скрипте или через переменную окружения PG_CONTAINER_NAME."
  exit 1
fi

if [ -z "$PG_USER" ]; then
  echo "ОШИБКА: Переменная POSTGRES_USER не найдена. Убедитесь, что она есть в .env файле или установлена в окружении."
  exit 1
fi

if [ -z "$PG_DB" ]; then
 echo "ОШИБКА: Переменная POSTGRES_DB не найдена. Убедитесь, что она есть в .env файле или установлена в окружении."
 exit 1
fi

if [ -z "$PG_PASSWORD" ]; then
 echo "ПРЕДУПРЕЖДЕНИЕ: Переменная POSTGRES_PASSWORD пуста в .env или окружении."
 echo "             Бэкап PostgreSQL может не сработать, если не настроена аутентификация без пароля (например, 'trust' или .pgpass)."
fi

if [ -z "$MINIO_VOLUME_NAME" ]; then
 echo "ОШИБКА: Переменная MINIO_VOLUME_NAME не установлена. Укажите ее в скрипте или через переменную окружения."
 exit 1
fi


echo "Определение пути к данным MinIO для тома '${MINIO_VOLUME_NAME}'..."
MINIO_DATA_HOST_PATH=$(docker volume inspect -f '{{ .Mountpoint }}' "${MINIO_VOLUME_NAME}" 2>/dev/null || echo "")

if [ -z "$MINIO_DATA_HOST_PATH" ]; then
  echo "ОШИБКА: Не удалось найти путь для Docker тома '${MINIO_VOLUME_NAME}'."
  echo "Убедитесь, что том существует (docker volume ls) и имя указано верно."
  exit 1
fi

if [ ! -d "$MINIO_DATA_HOST_PATH" ]; then
  echo "ОШИБКА: Путь к тому MinIO ('${MINIO_DATA_HOST_PATH}') не существует или не является директорией."
  echo "Возможно, требуются права sudo для доступа к директории Docker томов или том еще не создан."
  echo "Попробуйте запустить скрипт с sudo или проверьте вывод 'docker volume inspect ${MINIO_VOLUME_NAME}'"
  exit 1
fi

echo "Найден путь на хосте для данных MinIO: ${MINIO_DATA_HOST_PATH}"

mkdir -p "$BACKUP_DIR"
echo "Директория для бэкапов: $(realpath "$BACKUP_DIR")"

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

PG_BACKUP_FILENAME="pg_backup_${PG_DB}_${TIMESTAMP}.sql.gz"
PG_BACKUP_PATH="${BACKUP_DIR}/${PG_BACKUP_FILENAME}"

echo "------------------------------------"
echo "Начало бэкапа PostgreSQL базы данных: ${PG_DB}"
echo "Пользователь: ${PG_USER}"
echo "Целевой контейнер PostgreSQL: ${PG_CONTAINER_NAME}"
echo "Файл бэкапа: ${PG_BACKUP_FILENAME}"
echo "------------------------------------"

docker exec -t \
  -e "PGPASSWORD=${PG_PASSWORD}" \
  "$PG_CONTAINER_NAME" \
  pg_dump -U "$PG_USER" -d "$PG_DB" -Fp \
  | gzip > "$PG_BACKUP_PATH"

if [ $? -eq 0 ]; then
  echo "Бэкап PostgreSQL успешно создан и сохранен:"
  echo " -> ${PG_BACKUP_PATH}"
else
  echo "ОШИБКА: Не удалось создать бэкап PostgreSQL. Проверьте логи контейнера PostgreSQL (${PG_CONTAINER_NAME}) и права доступа/пароль."
  rm -f "$PG_BACKUP_PATH"
  exit 1
fi

MINIO_BACKUP_FILENAME="minio_backup_${TIMESTAMP}.tar.gz"
MINIO_BACKUP_PATH="${BACKUP_DIR}/${MINIO_BACKUP_FILENAME}"

echo "------------------------------------"
echo "Начало бэкапа данных MinIO (из тома '${MINIO_VOLUME_NAME}')"
echo "Исходная директория (хост): ${MINIO_DATA_HOST_PATH}"
echo "Файл бэкапа: ${MINIO_BACKUP_FILENAME}"
echo "------------------------------------"
echo "ВНИМАНИЕ: Бэкап MinIO выполняется путем копирования данных с хост-тома."
echo "          Для полной консистентности может потребоваться остановить MinIO на время бэкапа,"
echo "          особенно при активной записи."

echo "Выполнение tar для MinIO (может потребоваться sudo)..."
sudo tar -czf "$MINIO_BACKUP_PATH" -C "$MINIO_DATA_HOST_PATH" .

if [ $? -eq 0 ]; then
  echo "Бэкап данных MinIO успешно создан и сохранен:"
  echo " -> ${MINIO_BACKUP_PATH}"
else
  echo "ОШИБКА: Не удалось создать бэкап MinIO. Проверьте права доступа (возможно, нужен sudo), наличие свободного места или целостность тома."
  rm -f "$MINIO_BACKUP_PATH"
  exit 1
fi

if [ -f "$MINIO_BACKUP_PATH" ] && [ "$(stat -c '%U' "$MINIO_BACKUP_PATH")" == "root" ]; then
  echo "Смена владельца файла бэкапа MinIO на текущего пользователя..."
  sudo chown "$(id -u):$(id -g)" "$MINIO_BACKUP_PATH"
fi


RETENTION_DAYS=7 # Сколько дней хранить бэкапы
echo "------------------------------------"
echo "Поиск и удаление бэкапов старше ${RETENTION_DAYS} дней..."
find "$BACKUP_DIR" -type f \( -name "pg_backup_*.sql.gz" -o -name "minio_backup_*.tar.gz" \) -mtime +"$RETENTION_DAYS" -print -exec rm {} \;

echo "------------------------------------"
echo "Все бэкапы успешно завершены."
echo "------------------------------------"

exit 0
