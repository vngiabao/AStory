FROM gcr.io/distroless/java17

ENV TZ=Asia/Ho_Chi_Minh

WORKDIR /app

COPY target/ph-story-oauth2-service-1.0.jar ph-story-oauth2-service.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Ho_Chi_Minh","-jar","ph-story-oauth2-service.jar"]