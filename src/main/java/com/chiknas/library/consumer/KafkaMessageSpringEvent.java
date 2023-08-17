package com.chiknas.library.consumer;

import com.chiknas.library.domain.LibraryEvent;

public record KafkaMessageSpringEvent(long offset, Integer partition, LibraryEvent libraryEvent) {
}
