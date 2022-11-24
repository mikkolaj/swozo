FROM node:14 as build
WORKDIR /usr/src/client

COPY ./quiz-app/package.json /usr/src/client
COPY ./quiz-app/package-lock.json /usr/src/client

RUN npm install

COPY ./quiz-app /usr/src/client

RUN npm run build

FROM node:14
WORKDIR /usr/src/server

COPY ./quiz-app-server/package.json /usr/src/server
COPY ./quiz-app-server/package-lock.json /usr/src/server

RUN npm install

COPY ./quiz-app-server /usr/src/server

COPY --from=build /usr/src/client/build /usr/src/server/public/build

EXPOSE 8080

ENTRYPOINT [ "node", "server.js" ]
