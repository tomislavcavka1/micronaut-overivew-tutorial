# Dockerized Postgres

https://hub.docker.com/_/postgres

## Docker Compose
Execute from root directory:
```
docker-compose -f ./postgres/docker-compose.yml up
```

## Docker Swarm
Execute from root directory:
```
docker stack deploy -c ./postgres/docker-compose.yml postgres
```

Contains a volume for permanent storage of data. On system restart the data is available again.