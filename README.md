

Run postgres-test database
```bash
docker run --name postgres-test \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=work_db \
  -p 5432:5432 \
  -d postgres:latest

```