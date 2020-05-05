#!/usr/bin/env bash

POSTGRES="/usr/bin/psql"
DATABASE_NAME="sample_db"
DATABASE_USER="fujiwara"
DATABASE_PASS="fujiwara"
# {project_dir} から実行するのを期待している
WORK_DIR="${PWD}/sql/p"
FILE_PASS="${WORK_DIR}/create_database.sql"

$POSTGRES -h localhost -d $DATABASE_NAME -U $DATABASE_USER -W $DATABASE_PASS -p 45432 < $FILE_PASS
