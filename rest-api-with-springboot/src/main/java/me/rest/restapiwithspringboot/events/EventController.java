package me.rest.restapiwithspringboot.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }


    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto){
        //모델매퍼로 이벤트DTO에 있 는것을 EVENT 클래스의 인스턴스로 변환
        Event event = modelMapper.map(eventDto, Event.class);
        //save에 전달한 객체는 새로 만들어진 객체
        Event newEvent =  this.eventRepository.save(event);
        /*
        * Location URI 만들기
        * HATEOS가 제공하는 linkTo(), methodOn() 등 사용하여 uri 생성
        * */
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }

}