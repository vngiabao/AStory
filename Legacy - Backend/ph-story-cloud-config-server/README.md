# ph-story-cloud-config-server

Project ph-story-cloud-config-server

## Test
http://localhost:8082/ph-story-cloud-config-server/cds-tnd-oauth2-service/dev

## Define container environment variables using Secret data

- Link: https://kubernetes.io/docs/tasks/inject-data-application/distribute-credentials-secure/

- Step 1: Define an environment variable as a key-value pair in a Secret `name-tnd-config-server-git-username` and `name-tnd-config-server-git-password`
```bash
# Delete secret if exists
# kubectl delete secret <name-tnd-config-server-git-username|name-tnd-config-server-git-password>
kubectl create secret generic name-tnd-config-server-git-username --from-literal=key-tnd-config-server-git-username='devops_admin'
kubectl create secret generic name-tnd-config-server-git-password --from-literal=key-tnd-config-server-git-password='8ozxKTHwL4JW9BMvK2mB'
```

- Step 2: 
    - Assign the `name-tnd-config-server-git-username` value defined in the Secret to the `SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME` environment variable in the Pod specification
    - Assign the `name-tnd-config-server-git-password` value defined in the Secret to the `SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD` environment variable in the Pod specification
    
```yaml
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ph-story-cloud-config-server
  template:
    metadata:
      labels:
        app: ph-story-cloud-config-server
    spec:
      imagePullSecrets:
      - name: registry-dev
      containers:
      - name: ph-story-cloud-config-server
        image: registry-dev.hcmpc.com.vn/k8s/tnd/ph-story-cloud-config-server:v1
        ports:
        - containerPort: 9296
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: _JAVA_OPTIONS
          value: -Deureka.client.serviceUrl.defaultZone=http://ph-story-discovery-server:8761/eureka
        - name: EUREKA_CLIENT_REGISTERWITHEUREKA
          value: "true"
        - name: EUREKA_CLIENT_FETCHREGISTRY
          value: "true"
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_URI
          value: "https://gitlab.wsc.com.vn/sawaco/ph-story-store-properties-config-server.git"
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_CLONEONSTART
          value: "true"
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME
            valueFrom:
              secretKeyRef:
                name: name-tnd-config-server-git-username
                key: key-tnd-config-server-git-username
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD
            valueFrom:
              secretKeyRef:
                name: name-tnd-config-server-git-password
                key: key-tnd-config-server-git-password
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_CLONEONSTART
          value: "true"
        - name: SPRING_CLOUD_CONFIG_SERVER_GIT_SKIPSSLVALIDATION
          value: "true"
      hostAliases:
      - ip: 192.168.42.21
        hostnames:
        - gitlab.wsc.com.vn
```

- step 3: Display content of `SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME`
```shell script
kubectl exec -i -t <pod_name> -- /bin/sh -c 'echo $SPRING_CLOUD_CONFIG_SERVER_GIT_USERNAME'
```

## Auto refresh

1. Install RabbitMQ
- Install earlang: https://www.youtube.com/watch?v=V9DWKbalbWQ&ab_channel=TechnicalBabaji
- Install rabbitmq windows: https://www.rabbitmq.com/install-windows.html#installer
- Install rabbitmq ubuntu: https://www.vultr.com/docs/install-rabbitmq-server-ubuntu-20-04-lts/
- Account Rabbitmq (192.168.42.44): admin/R@bbitmq#2022

2. Code
Link: https://www.youtube.com/watch?v=aC_siBP8rx8&t=796s&ab_channel=SpringDeveloper

```bash
# Refesh from client
curl -H"content-type: application/json" -d{} http://localhost:8080/actuator/refresh

# Refresh from config server
curl -H"content-type: application/json" -d{} http://localhost:8182/actuator/busrefresh
```

## Install RabbitMQ steps

First of all run the system update using the below command
```shell script
sudo apt update -y
sudo apt install curl gnupg -y
```

- Step 1. Import RabbitMQ GPG Signing key
```shell script
# Run the following commands to import Erlang repository GPG key.
curl -fsSL https://github.com/rabbitmq/signing-keys/releases/download/2.0/rabbitmq-release-signing-key.asc | sudo apt-key add -

# Also, install the HTTPS transport if missing
sudo apt-get install apt-transport-https
```

- Step 2. Add RabbitMQ & Erlang 23.x repository
```shell script
# Create Rabbitmq repository file.
sudo nano /etc/apt/sources.list.d/bintray.rabbitmq.list

# With below content - /etc/apt/sources.list.d/bintray.rabbitmq.list
deb https://dl.bintray.com/rabbitmq-erlang/debian focal erlang
deb https://dl.bintray.com/rabbitmq/debian bionic main

# Save and close the file.
```

- Step 3. Install Erlang 23.x and RabbitMQ
```shell script
# Update the system packages list first, by running the below command
sudo apt update -y

# Now, install Erlang package and RabbitMQ server
sudo apt install rabbitmq-server -y --fix-missing

# verify the installation, by running the below command
erl -v
```

- Step 4. Manage RabbitMQ service
```shell script
# Start service rabbitmq-server
sudo systemctl start rabbitmq-server

# Enable service rabbitmq-server
sudo systemctl enable rabbitmq-server

# Check status rabbitmq-server
sudo systemctl status rabbitmq-server
```

- Step 5. Create User in RabbitMQ server.
```shell script
# Create admin user
sudo rabbitmqctl add_user admin password

# Provide the tags to created user
sudo rabbitmqctl set_user_tags admin administrator

# Provide the permission.
sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
```

- Step 6. Enable the RabbitMQ Web Management Console.
```shell script
sudo rabbitmq-plugins enable rabbitmq_management
```

- Step 7. Open the port number in UFW firewall.
```shell script
# Port web
sudo ufw allow 15672/tcp

# Port API
sudo ufw allow 5672/tcp
```

- Step 8. Access the RabbitMQ Web -Interface.
```shell script
http://server-ip:15672
```

- Utils
```shell script
# Delete the RabbitMQ User.
rabbitmqctl delete_user user_name

# To Change the RabbitMQ User Password.
rabbitmqctl change_password user_name password_here

# To create a new Virtualhost.
rabbitmqctl add_vhost /new_vhost_name

# To list the available Virtualhosts.
rabbitmqctl list_vhosts

# To delete a virtualhost.
rabbitmqctl delete_vhost /vhost_name

# To provide Grant user permissions for vhost.
rabbitmqctl set_permissions -p /vhost_name user_name ".*" ".*" ".*"

# To list vhost permissions.
rabbitmqctl list_permissions -p /vhost_name

# List user permissions.
rabbitmqctl list_user_permissions user_name

# To delete user permissions.
rabbitmqctl clear_permissions -p /vhost_name user_name
```


