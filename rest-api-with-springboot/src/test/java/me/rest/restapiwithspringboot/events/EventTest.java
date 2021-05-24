package me.rest.restapiwithspringboot.events;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public  void builder(){
        Event event = Event.builder( )
                .name("Spring REST API")
                .description("REST API development with spring boot")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        //Given
        Event event = new Event();
        String name = "Event";
        String description = "Spring";

        //When
        event.setName(name);
        event.setDescription(description);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

}