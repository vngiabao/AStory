FROM eclipse-temurin:17-jre

ENV TZ=Asia/Ho_Chi_Minh

WORKDIR /app

COPY target/ph-story-cloud-config-server-1.0.jar ph-story-cloud-config-server.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Ho_Chi_Minh","-jar","ph-story-cloud-config-server.jar"]