# Camunda demo process

## How to run

Start the local environment with 
```bash
docker compose up -d
``` 
and run 
```bash
curl http://localhost:8080/start
```
to start the process. To stop it run
```bash
docker compose down -v
```

You can log into the different services with the user demo and password demo:
- Operate: http://localhost:8081
- Tasklist: http://localhost:8082
- Optimize: http://localhost:8083
- Identity: http://localhost:8084
- Elasticsearch: http://localhost:9200

For Keycloak you can log in with the user admin and password admin: http://localhost:18080/auth/.

    
