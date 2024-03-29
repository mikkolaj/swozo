# Swozo web-server

---

## Configuring project

You have to provide file system path to the private key for interacting with GCP - its location should be specified in `GOOGLE_APPLICATION_CREDENTIALS` environment variable (same one as for the orchestrator can be used).

Application properties need to be adjusted according to your needs. It's recommended to specify them using env variables declared in `application.properties` file, by replacing them with your own values or by utilizing spring profiles, for development use `application-dev.properties` with `dev` [profile](https://stackoverflow.com/questions/39738901/how-do-i-activate-a-spring-boot-profile-when-running-from-intellij).

Requirements:
1. `gcp.storage.project` must contain your GCP project name
2. `gcp.storage.zone` must contain desired zone to create resources in
3. `storage.web-bucket.name` must contain name of the bucket used for storage (it must be globally unique, max 64 chars long)
4. `orchestrator.secret` must contain secret key used for orchestrator authentication, same one must be configured on orchestrator
5. `initial-admin.email` must contain email of first system admin, who should then reset his password using web-app

## Local development

For now running server via Intellij is recommended. Server should be available at `localhost:5000`.

---

### API

We use Swagger(Open API) for documentation and code generation.
To see autogenerated documentation open http://localhost:5000/swagger-ui/index.html.

With server working run `yarn gen` in frontend main directory to generate typescript stubs.