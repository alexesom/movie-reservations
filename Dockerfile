FROM sbtscala/scala-sbt:openjdk-18.0.2.1_1.8.1_2.13.10
RUN mkdir -p /app
WORKDIR /app
COPY . /app
EXPOSE 8080
COPY entrypoint.sh /entrypoint.sh