FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/\*.jar

ENV GCP_SSH_KEY_PATH auth/orchestrator_id_rsa
ENV GOOGLE_APPLICATION_CREDENTIALS auth/orchestrator-key.json

ENV JUPYTER_PLAYBOOK_PATH=playbook/software/jupyter/prepare-and-run-jupyter.yml
COPY src/main/resources/provisioning/administration provisioning/administration
COPY src/main/resources/provisioning/software provisioning/software

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]