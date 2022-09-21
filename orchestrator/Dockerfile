FROM eclipse-temurin:18.0.2.1_1-jre
ARG JAR_FILE=build/libs/\*.jar

ENV GCP_SSH_KEY_PATH /auth/orchestrator_id_rsa
ENV GOOGLE_APPLICATION_CREDENTIALS /auth/orchestrator-key.json

ENV JUPYTER_PLAYBOOK_PATH=/provisioning/software/jupyter/prepare-and-run-jupyter.yml
COPY ./src/main/resources/provisioning/administration /provisioning/administration
COPY ./src/main/resources/provisioning/software /provisioning/software

COPY ${JAR_FILE} orchestrator.jar
ENTRYPOINT ["java","--enable-preview","-jar","/orchestrator.jar"]
