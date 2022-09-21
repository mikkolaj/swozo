# Swozo orchestrator

## Local development
Before running the app make sure to run build/jar Gradle task in swozo-commons.

Additionally, you have to provide two configuration files:
1. Private key for interacting with GCP - its location should be specified in GOOGLE_APPLICATION_CREDENTIALS environment variable.
2. Private key for connecting to cloud VMs - its location should be specified in gcp.ssh.key-path application property

Application properties need to be adjusted according to your needs. It's possible to specify them using env variables
declared in application.properties file, by replacing them with your own values or by utilizing spring profiles (https://stackoverflow.com/questions/39738901/how-do-i-activate-a-spring-boot-profile-when-running-from-intellij)

Requirements:
1. gcp.project must contain your GCP project name
2. gcp.zone must contain desired zone to create resources in
3. gcp.ssh.user must contain a user that will be used as a service account to perform installation of required software components
4. gcp.ssh.key-path must contain path to private key described above
5. properties ending with "playbook-path" must contain paths to playbooks responsible for installing required components

Inside example-requests directory you'll find Postman collection you can use to test the app.

## Running in Docker
Running Orchestrator as a Docker container requires 2 phases:
1. Building the image - run ```docker build -t swozo/orchestrator .```
2. Running the image - you need to specify 3 environment variables, and mount one directory containing two configuration
   files mentioned above. Example command will look like this:
```
docker run 
-e GCP_PROJECT=someProject
-e GCP_ZONE=someZone
-e GCP_SSH_USER=someUser

```
