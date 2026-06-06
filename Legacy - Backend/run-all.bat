@echo off

echo Starting Discovery Server...
start "discovery" cmd /k "cd ph-story-discovery-server && mvn spring-boot:run"

timeout /t 15

echo Starting Cloud Config Server...
start "config" cmd /k "cd ph-story-cloud-config-server && mvn spring-boot:run"

timeout /t 15

echo Starting API Gateway...
start "gateway" cmd /k "cd ph-story-api-gateway && mvn spring-boot:run"

echo Starting User Service...
start "users" cmd /k "cd ph-story-users-service && mvn spring-boot:run"

echo Starting Oauth2 Service...
start "oauth2" cmd /k "cd ph-story-oauth2-service && mvn spring-boot:run"

echo Starting MVP Service...
start "mvp" cmd /k "cd ph-story-mvp-service && mvn spring-boot:run"

echo Starting SeaweedFS...
start "seaweedfs" cmd /k "cd ph-story-seaweedfs-service && mvn spring-boot:run"

echo ==============================
echo ALL SERVICES STARTED
echo ==============================

pause