FROM gcr.io/distroless/java17

ENV TZ=Asia/Ho_Chi_Minh

WORKDIR /app

COPY target/ph-story-discovery-server-1.0.jar ph-story-discovery-server.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Ho_Chi_Minh","-jar","ph-story-discovery-server.jar"]