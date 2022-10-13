# SWOZO

### Running both database and Orchestrator

In the uppermost directory run:

```shell
docker-compose up
```

### Running only database

It might be enough if you want to run orchestrator from Intellij. In the database directory run:

```shell
docker-compose up
```

---

### Alternatively you can run dependencies individually:

#### Creating common network for DB and other Docker containers

```shell
docker network create -d bridge swozo
```

#### Running database without compose:

```shell
docker run -d \
    -p 5432:5432 \
    --name swozo-db \
    --network swozo
    -e POSTGRES_DB=swozo-web-db \
    -e POSTGRES_PASSWORD=mysecretpassword \
    -e ORCHESTRATOR_PASSWORD=mysecretpassword2 \
    swozo/postgres:latest
    

```
