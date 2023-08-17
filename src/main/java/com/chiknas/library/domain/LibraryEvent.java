package com.chiknas.library.domain;


public record LibraryEvent(
    Integer libraryEventId,
    LibraryEventType libraryEventType,
    Book book
) {
}
