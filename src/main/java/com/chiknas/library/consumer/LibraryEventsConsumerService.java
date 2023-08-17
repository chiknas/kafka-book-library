package com.chiknas.library.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Service;

import com.chiknas.library.domain.LibraryEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;

@Slf4j
@Service
public class LibraryEventsConsumerService implements ConsumerSeekAware {

  private final ObjectMapper objectMapper;

  @Getter
  private final List<KafkaMessageSpringEvent> events = new ArrayList<>();

  public LibraryEventsConsumerService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "library-events", groupId = "library-events-listener-group")
  public void onMessage(ConsumerRecord<Integer, String> libraryEventValue) throws JsonProcessingException {
    Integer partition = libraryEventValue.partition();
    long offset = libraryEventValue.offset();
    LibraryEvent libraryEvent = objectMapper.readValue(libraryEventValue.value(), LibraryEvent.class);
    log.info("CONSUMER - Received a new event: {} ", libraryEvent);
    events.add(new KafkaMessageSpringEvent(offset, partition, libraryEvent));
  }

  @Override
  public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
    assignments.keySet().forEach(partition -> callback.seek("library-events", partition.partition(), 0));
  }
}
