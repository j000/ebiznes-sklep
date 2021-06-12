FROM node:16 as front-end-builder
USER node
WORKDIR /home/node
ENV NODE_ENV=production
RUN ["mkdir", "-p", "./.yarn/"]
COPY front/package.json ./package.json
COPY front/yarn.lock ./
COPY front/.yarnrc.yml ./
COPY --chown=node front/.pnp.js ./
COPY --chown=node front/.yarn/ ./.yarn/
RUN ["yarn", "install", "--immutable", "--inline-builds"]
COPY front/ .
RUN ["yarn", "build"]

FROM nginx:stable-alpine as front-end
COPY --from=front-end-builder /home/node/dist /usr/share/nginx/html
COPY .nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80

FROM docker.io/j000/ebiznes:sbt as back-end-builder
RUN ["mkdir", "-p", "./project"]
COPY build.sbt .
COPY project/plugins.sbt project/build.properties ./project/
COPY app ./app/
COPY conf ./conf/
COPY styles/ ./app/assets/styles/
RUN ["sbt", "assembly"]

# FROM adoptopenjdk/openjdk8:alpine as back-end
# USER nobody
# WORKDIR /app
# COPY --chown=nobody --from=back-end-builder /home/app/project/target/universal/stage /app/
# EXPOSE 9000
# RUN ["/app/bin/sklep"]

FROM gcr.io/distroless/java:8 as back-end
USER nonroot
WORKDIR /home/nonroot
COPY --chown=nonroot --from=back-end-builder /home/app/project/target/scala-2.13/sklep-assembly-1.0-SNAPSHOT.jar ./sklep.jar
EXPOSE 9000
CMD ["./sklep.jar"]
