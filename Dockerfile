FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/kbqa_tech_web-0.0.1-SNAPSHOT.jar app.jar
#ADD out/artifacts/kbqa_tech_web_jar/kbqa_tech_web.jar app.jar
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8085