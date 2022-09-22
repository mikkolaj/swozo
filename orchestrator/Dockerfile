FROM eclipse-temurin:18.0.2.1_1-jre

ARG JAR_FILE=build/libs/\*.jar

RUN ["apt","update"]
RUN ["apt","install","sshpass","-y"]
RUN ["apt","install","python3","-y"]
RUN ["curl","https://bootstrap.pypa.io/get-pip.py","-o","get-pip.py"]
RUN ["python3","get-pip.py","--user"]
RUN ["python3","-m","pip","install","--user","ansible"]

ENV PATH="${PATH}:~/.local/bin"
ENV GCP_SSH_KEY_PATH /auth/orchestrator_id_rsa
ENV GOOGLE_APPLICATION_CREDENTIALS /auth/orchestrator-key.json

ENV JUPYTER_PLAYBOOK_PATH=/provisioning/software/jupyter/prepare-and-run-jupyter.yml
COPY ./src/main/resources/provisioning/administration /provisioning/administration
COPY ./src/main/resources/provisioning/software /provisioning/software

COPY ${JAR_FILE} /app/orchestrator.jar
ENTRYPOINT ["java","--enable-preview","-jar","/app/orchestrator.jar"]
