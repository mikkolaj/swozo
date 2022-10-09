# SWOZO

## Setting up the database

In ./database directory run:

```shell
./build.sh (Linux)
or
.\build.bat (Windows)
```
then
```shell
docker-compose up
```

---

### Alternatively you can run dependencies individually:

#### Running database:

```shell
docker run -d \
    -p 5432:5432 \
    --name swozo-db \
    -e POSTGRES_DB=swozo-web-db \
    -e POSTGRES_PASSWORD=mysecretpassword \
    -e ORCHESTRATOR_PASSWORD=mysecretpassword2 \
    swozo/postgres:latest
```
