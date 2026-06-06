# ph-story-oauth2-service

Project ph-story-oauth2-service

## Build module

```bash
# Build using package
mvn clean package -DskipTests=true

# Build with specific project
mvn -DskipTests=true --projects user.core,user.oauth2.0 -am clean install
```

## Define container environment variables using Secret data

- Link: https://kubernetes.io/docs/tasks/inject-data-application/distribute-credentials-secure/

- Step 1: Define an environment variable as a key-value pair in a Secret
```bash
kubectl create secret generic name-tnd-jasypt-encryptor-password --from-literal=key-tnd-jasypt-encryptor-password='pwdCdsHCMCIT'
```

- Step 2: Assign the `name-tnd-jasypt-encryptor-password` value defined in the Secret to the `JASYPT_ENCRYPTOR_PASSWORD` environment variable in the Pod specification
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ph-story-oauth2-service-deployment
  labels:
    app: ph-story-oauth2-service
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ph-story-oauth2-service
  template:
    metadata:
      labels:
        app: ph-story-oauth2-service
    spec:
      containers:
      - name: ph-story-oauth2-service
        image: registry.ph.com.vn/k8s/tnd/ph-story-oauth2-service:v1
        ports:
        - containerPort: 8084
        env:
        - name: JASYPT_ENCRYPTOR_PASSWORD
          valueFrom:
            secretKeyRef:
              name: name-tnd-jasypt-encryptor-password
              key: key-tnd-jasypt-encryptor-password
      ImagePullSecrets:
      - name: registry
```

- step 3: Display content of `JASYPT_ENCRYPTOR_PASSWORD`
```shell script
kubectl exec -i -t <pod_name> -- /bin/sh -c 'echo $JASYPT_ENCRYPTOR_PASSWORD'
```

### Resource

https://www.codejava.net/frameworks/spring-boot/spring-security-add-roles-to-user-examples
https://github.com/Baeldung/spring-security-registration

### Test
```bash
curl -X POST "http://localhost:8084/oauth/token" -H "accept: application/json" -H "Authorization: Basic Y2RzLXRuZC1jbGllbnQtaWQ6Y2RzLXRuZC1jbGllbnQtc2VjcmV0" -H "content-type: application/x-www-form-urlencoded" -d "grant_type=password&username=admin&password=123"
```
