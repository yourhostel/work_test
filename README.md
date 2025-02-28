
```text
work
│── devops/
│   ├── docker/
│   │   ├── Dockerfile.app
│   │   ├── Dockerfile.postgres
│   ├── app_manifest.yaml
│   ├── postgres_manifest.yaml
│── src/
│   └── main/
│       ├── java/
│       ├── resources/
│           ├── application.properties  # Конфігураційний файл Spring Boot
│── work-chart/
│   ├── charts/ не 
│   ├── templates/
│   │   ├── deployment.yaml               # Deployment для Spring Boot
│   │   ├── statefulset.yaml              # StatefulSet для PostgreSQL
│   │   ├── service.yaml                   
│   │   ├── secret.yaml                    
│   ├── Chart.yaml
│   ├── values.yaml
│   ├── .helmignore
```

- Run postgres-test database
```bash
docker run --name postgres-test \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=mypass \
  -e POSTGRES_DB=work_db \
  -p 5432:5432 \
  -d postgres:latest
```


- create secrets database
```bash
kubectl create namespace work

# switching between contexts
kubectl config set-context --current --namespace=work
kubectl config set-context --current --namespace=default


kubectl create secret generic postgres-secret \
  --from-literal=DATABASE_PASSWORD=mypass \
  --from-literal=DATABASE_USER=myuser \
  --from-literal=DATABASE_URL="jdbc:postgresql://postgres-service:5432/work_db" \
```
- example delete secret
```bash
kubectl delete secret postgres-secret
```

- check secret database
```bash
kubectl get secret postgres-secret -n work -o yaml
```

- delete all images and containers  
```bash
# stop all containers
docker stop $(docker ps -aq)
# delete all containers
docker rm $(docker ps -aq)
# Delete all images
docker rmi $(docker images -q)
```

- build definition from Dockerfile.app
```bash
docker build -t rest-spring-app -f devops/docker/Dockerfile.app .
```

- build definition from Dockerfile.postgres
```bash
docker build -t rest-postgres -f devops/docker/Dockerfile.postgres .
```

- save images to file 
```bash
docker save -o rest-postgres.tar rest-postgres
docker save -o rest-spring-app.tar rest-spring-app
```

- import file to K3s
```bash
sudo k3s ctr images import rest-postgres.tar
sudo k3s ctr images import rest-spring-app.tar
sudo k3s ctr images list | grep rest
```
```bash
kubectl apply -f ./devops/postgres_manifest.yaml
kubectl delete -f ./devops/postgres_manifest.yaml
kubectl apply -f ./devops/app_manifest.yaml
kubectl delete -f ./devops/app_manifest.yaml

kubectl get pods
kubectl get services
kubectl get pvc
kubectl delete all --all
```

- check endpoint database
```bash
kubectl run -it --rm psql-client --image=postgres:latest -- bash -c "export PGPASSWORD=mypass && psql -h postgres-service -U myuser work_db"
```

- check logs app
```bash
kubectl logs springboot-*
```

- check environment
```bash
kubectl exec -it springboot-app-cf86968bb-f542m -- printenv | grep -i database
kubectl exec -it springboot-app-cf86968bb-f542m -- printenv | grep -i database
```

- proxy traffic from a local port
```bash
kubectl port-forward svc/springboot-service 8080:8080


sudo lsof -i :8090
sudo kill -9 <PID>
```

# Helm

```bash
# Install the Helm chart (first-time deployment)
helm install work-chart ./work-chart

# Check how Helm renders templates before applying them (useful for debugging)
helm template work-chart ./work-chart | grep -A 5 "ports:"

# Upgrade or install the Helm chart (deploys PostgreSQL & app)
helm upgrade --install work-chart ./work-chart

# Delete the running PostgreSQL pod (forces it to restart with new settings)
kubectl delete pod -l app=postgres -n work

# View logs of the running PostgreSQL pod (check for errors or success messages)
kubectl logs -l app=postgres -n work

helm uninstall work-chart
```

#### To start with the creation of a secret is the surest way


### Secure way to create a Kubernetes secret

The safest way to handle secrets is by using `read -s` (to avoid storing passwords in history) or a separate `secrets.yaml` file, which should be added to `.gitignore`.

#### Using `read -s` to prevent password exposure in history
```bash
read -sp "Enter DB Password: " DB_PASSWORD
helm install work-chart ./work-chart \
  --set DATABASE_PASSWORD="$DB_PASSWORD" \
  --set DATABASE_USER="myuser" \
  --set DATABASE_URL="jdbc:postgresql://postgres-service:5432/work_db"
```

```bash
helm get values work-chart
```