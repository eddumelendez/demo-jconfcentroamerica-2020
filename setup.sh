export PROJECT_ID=
export GCP_REGION=
export DATABASE_INSTANCE_NAME=
export DATABASE_NAME=
export ROOT_PASSWORD=
export DATABASE_USER=
export DATABASE_PASSWORD=
export REDIS_INSTANCE_NAME=

gcloud services enable sqladmin.googleapis.com

gcloud sql instances create ${DATABASE_INSTANCE_NAME} \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=${GCP_REGION} \
  --root-password=${ROOT_PASSWORD}

gcloud sql databases create ${DATABASE_NAME} --instance ${DATABASE_INSTANCE_NAME}

export INSTANCE_CONNECTION_NAME=$(gcloud sql instances describe ${DATABASE_INSTANCE_NAME} --format=json | jq -r '.connectionName')

gcloud sql users create ${DATABASE_USER} \
  --instance=${DATABASE_INSTANCE_NAME} \
  --password=${DATABASE_PASSWORD}

docker run -d \
  -v $(pwd)/cloudsql-client-service-account-key.json:/config \
  -p 127.0.0.1:3306:3306 \
  gcr.io/cloudsql-docker/gce-proxy:1.19.0 /cloud_sql_proxy \
  -instances=${INSTANCE_CONNECTION_NAME}=tcp:0.0.0.0:3306 -credential_file=/config

mysql -u ${DATABASE_USER} -p${DATABASE_PASSWORD} --host 127.0.0.1 < src/test/resources/sql/schema.sql
mysql -u ${DATABASE_USER} -p${DATABASE_PASSWORD} --host 127.0.0.1 < src/test/resources/sql/data.sql

gcloud services enable redis.googleapis.com

gcloud redis instances create ${REDIS_INSTANCE_NAME} --size=1 --region=${GCP_REGION}

export GCP_REDIS_IP=$(gcloud redis instances describe ${REDIS_INSTANCE_NAME} --region=${GCP_REGION} --format="json" | jq -r '.host')

gcloud services enable vpcaccess.googleapis.com

gcloud compute networks vpc-access connectors create redis-connector --network default --region ${GCP_REGION} --range 10.9.0.0/28

export REDIS_CONNECTOR_NAME=$(gcloud compute networks vpc-access connectors list --region ${GCP_REGION} --format="json" | jq -r '.[0].name')

./mvnw spring-boot:build-image -DskipTests \
  -Dspring-boot.build-image.imageName=gcr.io/${PROJECT_ID}/demo-jconfcentroamerica-2020:0.0.1-SNAPSHOT -Pgcp

docker push gcr.io/${PROJECT_ID}/demo-jconfcentroamerica-2020:0.0.1-SNAPSHOT

gcloud beta run deploy demo-service --image gcr.io/${PROJECT_ID}/demo-jconfcentroamerica-2020:0.0.1-SNAPSHOT \
  --min-instances 1 \
  --allow-unauthenticated \
  --platform managed \
  --port 8080 \
  --memory 1024Mi \
  --region ${GCP_REGION} \
  --add-cloudsql-instances ${INSTANCE_CONNECTION_NAME} \
  --vpc-connector ${REDIS_CONNECTOR_NAME} \
  --update-env-vars SPRING_CLOUD_GCP_SQL_DATABASE_NAME=${DATABASE_NAME},SPRING_CLOUD_GCP_SQL_INSTANCE_CONNECTION_NAME=${INSTANCE_CONNECTION_NAME},SPRING_DATASOURCE_USERNAME=${DATABASE_USER},SPRING_DATASOURCE_PASSWORD=${DATABASE_PASSWORD},SPRING_REDIS_HOST=${GCP_REDIS_IP}

