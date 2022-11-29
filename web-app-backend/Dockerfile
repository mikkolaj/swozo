FROM eclipse-temurin:18.0.2.1_1-jre

EXPOSE 5000

ENV ENABLE_BOOTSTRAPPING=false

COPY ./build/libs/web-app-backend.jar /app/web-app-backend.jar

ENTRYPOINT [ \
    "java", \
    "--enable-preview", \
    "-Duser.timezone=UTC", \
    "-Dspring.profiles.active=prod", \
    "-jar","/app/web-app-backend.jar" \
]
