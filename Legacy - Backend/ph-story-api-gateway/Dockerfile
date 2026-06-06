FROM gcr.io/distroless/java17

ENV TZ=Asia/Ho_Chi_Minh

WORKDIR /app

COPY target/ph-story-api-gateway-1.0.jar ph-story-api-gateway.jar

ENTRYPOINT ["java","-Duser.timezone=Asia/Ho_Chi_Minh","-jar","ph-story-api-gateway.jar"]