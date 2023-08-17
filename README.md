# Getting Started

* Start up Kafka cluster
```
docker-compose up -d
```

* Start the application
```
./gradlew bootRun
```

* Go to `http://localhost:8080`


### Optional kafka message producer container
```
docker exec --interactive --tty kafka1  \
kafka-console-prodecer --bootstrap-server localhost:9092,kafka2:19093,kafka3:19094 \
                       --topic library-events \
                       --from-beginning
```

### Optional kafka message consumer container
```
docker exec --interactive --tty kafka1  \
kafka-console-consumer --bootstrap-server localhost:9092,kafka2:19093,kafka3:19094 \
                       --topic library-events \
                       --from-beginning
```

