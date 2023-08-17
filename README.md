# Getting Started

## Start up Kafka cluster
```
docker-compose up -d
```

## Optional kafka consumer container
```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server localhost:9092,kafka2:19093,kafka3:19094 \
                       --topic library-events \
                       --from-beginning
```

