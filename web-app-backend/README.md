# Swozo web-server

## Local developement

In this directory run:
```shell
docker-compose up
```

For now running server via Intellij is recommended. Server should be available at `localhost:5000`.

---
### Alternatively you can run dependencies individually:

#### Running database:
```shell
docker run -d \
    -p 5432:5432 \
    --name swozo-web-postgres \
    -e POSTGRES_DB=swozo-web-db \
    -e POSTGRES_PASSWORD=mysecretpassword \
    postgres:14-alpine
```

---