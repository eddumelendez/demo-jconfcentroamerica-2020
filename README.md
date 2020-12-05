# JConfCentroAmerica's demo

NOTE: `gcloud` must be installed.

First, we need to set the following environment variables

```
export PROJECT_ID=
export GCP_REGION=
export DATABASE_INSTANCE_NAME=
export DATABASE_NAME=
export ROOT_PASSWORD=
export DATABASE_USER=
export DATABASE_PASSWORD=
export REDIS_INSTANCE_NAME=
```

We need the service account to perform the scripts from our local machine

```
gcloud iam service-accounts create cloudsql-client --display-name "cloudsql-client-sa"

export SERVICE_ACCOUNT_EMAIL=$(gcloud iam service-accounts list --filter="displayName:cloudsql-client-sa" --format='value(email)')

gcloud projects add-iam-policy-binding ${PROJECT_ID} \
  --member serviceAccount:${SERVICE_ACCOUNT_EMAIL} \
  --role roles/cloudsql.client

gcloud iam service-accounts keys create cloudsql-client-service-account-key.json \
  --iam-account ${SERVICE_ACCOUNT_EMAIL}
```

Now, we can perform the script `./setup` in order to create the DB instance and the database. Later, the image would be created and deployed in Google Cloud Run.

In order to test it locally using the `docker-compose.yml` file you can build the following image `./mvnw spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=demo-jconfcentroamerica-2020:0.0.1-SNAPSHOT` and then `docker-compose up`
