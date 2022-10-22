FROM elixir:1.12.3-alpine

COPY . .

RUN apk update; \
    apk add bash openssl build-base ca-certificates
#    apk add openrc --no-cache; \
#    apk add docker docker-compose ; \
RUN bash -c 'mkdir -p ~/.jitsi-meet-cfg/{web/letsencrypt,web/keys,transcripts,prosody/config,prosody/prosody-plugins-custom,jicofo,jvb,jigasi,jibri}'; \
    openssl req -newkey rsa:2048 -nodes -keyout key.pem -x509 -days 365 -out certificate.pem -subj "/C=US/ST=Denial/L=Springfield/O=Dis/CN=www.example.com";\
    cp certificate.pem /usr/local/share/ca-certificates/; \
    update-ca-certificates; \
    mv certificate.pem cert.crt; \
    mv key.pem cert.key; \
    mv cert.crt cert.key ~/.jitsi-meet-cfg/web/keys

RUN mix local.hex --force; \
    mix local.rebar --force;

#EXPOSE 8443

ENTRYPOINT ["./startup.sh"]
