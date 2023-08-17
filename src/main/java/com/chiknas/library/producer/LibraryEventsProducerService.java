package com.chiknas.library.producer;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.chiknas.library.domain.LibraryEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LibraryEventsProducerService {

  @Value("${spring.kafka.topic}")
  private String topicName;
  private final KafkaTemplate<Integer, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public LibraryEventsProducerService(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendLibraryEvent(LibraryEvent libraryEvent){
    try{
      CompletableFuture<SendResult<Integer, String>> send = kafkaTemplate.send(topicName, libraryEvent.libraryEventId(), objectMapper.writeValueAsString(libraryEvent));
      send.whenComplete((result, exception) -> {
        if(exception != null){
          log.error("Failed to send message to kafka: {}", exception.getMessage(), exception);
        }else{
          log.info("PRODUCER - Successfully sent the following message to kafka: {}", result);

        }
      });
    }catch (Exception e){
      throw new RuntimeException(e);
    }
  }
}
