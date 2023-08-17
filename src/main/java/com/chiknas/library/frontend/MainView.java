package com.chiknas.library.frontend;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.chiknas.library.consumer.KafkaMessageSpringEvent;
import com.chiknas.library.consumer.LibraryEventsConsumerService;
import com.chiknas.library.domain.Book;
import com.chiknas.library.domain.LibraryEvent;
import com.chiknas.library.domain.LibraryEventType;
import com.chiknas.library.producer.LibraryEventsProducerService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@UIScope
@Route("")
public class MainView extends AppLayout {
  private AtomicInteger integer = new AtomicInteger();

  private final LibraryEventsConsumerService libraryEventsConsumerService;
  private final Grid<KafkaMessageSpringEvent> libraryEventGrid;

  public MainView(LibraryEventsProducerService libraryEventsService, LibraryEventsConsumerService libraryEventsConsumerService) {
    this.libraryEventsConsumerService = libraryEventsConsumerService;
    H2 title = new H2("Book Library");
    FlexLayout centeredLayout = new FlexLayout();
    centeredLayout.setSizeFull();
    centeredLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    centeredLayout.setAlignItems(FlexComponent.Alignment.CENTER);
    centeredLayout.add(title);
    addToNavbar(centeredLayout);


    H3 newEntry = new H3("New entry");
    IntegerField kafkaPartition = new IntegerField("Kafka message key");
    TextField name = new TextField("Title");
    TextField author = new TextField("Author");
    Button submit = new Button("Submit");
    submit.addClickListener((ComponentEventListener<ClickEvent<Button>>) event ->
        libraryEventsService.sendLibraryEvent(new LibraryEvent(kafkaPartition.getValue(), LibraryEventType.NEW, new Book((int) (Math.random() * 100), name.getValue(), author.getValue())))
    );
    VerticalLayout formLayout = new VerticalLayout(newEntry, name, author, kafkaPartition, submit);
    formLayout.getStyle().set("padding", "2em").set("width", "250px");

    libraryEventGrid = new Grid<>();
    libraryEventGrid.getStyle().set("margin-top", "2em");
    libraryEventGrid.addColumn(KafkaMessageSpringEvent::offset).setHeader("Kafka Partition offset");
    libraryEventGrid.addColumn(KafkaMessageSpringEvent::partition).setHeader("Kafka Partition");
    libraryEventGrid.addColumn(x -> x.libraryEvent().book().name()).setHeader("Title");
    libraryEventGrid.addColumn(x -> x.libraryEvent().book().author()).setHeader("Author");

    setContent(new HorizontalLayout(formLayout, libraryEventGrid));
  }

  @Scheduled(fixedDelay = 200)
  public void newKafkaMessage() {
    getUI().ifPresent(ui -> {
      ui.access((Command) () -> {
        List<KafkaMessageSpringEvent> events = libraryEventsConsumerService.getEvents();
        if(events.size() != integer.get()){
          integer.set(events.size());
          libraryEventGrid.setItems(events);
        }
      });
    });
  }
}
