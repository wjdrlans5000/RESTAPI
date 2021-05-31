package me.rest.restapiwithspringboot.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventVaildator eventVaildator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventVaildator eventVaildator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventVaildator = eventVaildator;
    }


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        //@Valid를 사용하면 Handler Method에서 데이터를 바인딩시 검증을 진행한다.
        //이때 애노테이션들의 정보를 참고해서 검증을 수행한다.
        //eventDto 바인딩시 에러발생할경우 Errors객체로 바인딩
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        eventVaildator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }


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
