

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