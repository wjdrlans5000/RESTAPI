package me.rest.restapiwithspringboot.events;

import me.rest.restapiwithspringboot.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
            return badRequest(errors);
        }

        eventVaildator.validate(eventDto, errors);
        if(errors.hasErrors()){
        return badRequest(errors);
    }


        //모델매퍼로 이벤트DTO에 있 는것을 EVENT 클래스의 인스턴스로 변환
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        //save에 전달한 객체는 새로 만들어진 객체
        Event newEvent =  this.eventRepository.save(event);
        /*
        * Location URI 만들기
        * HATEOS가 제공하는 linkTo(), methodOn() 등 사용하여 uri 생성
        * */
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
//        RepresentationModel를  상속받으면 add Method를 통해 링크정보를 추가할 수 있다.
//        withRel(): 이 링크가 리소스와 어떤 관계에 있는지 관계를 정의할 수 있다.
//        withSelfRel(): 리소스에 대한 링크를 type-safe 한 method로 제공한다.
//        Relation과 HREF 만 제공.
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        //self 추가 -> EventResource에서 추가시킴
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        //profile Link 추가
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    //Pageable > paging 과 관련된 정보들을 받아올 수 있음.
    @GetMapping
    public  ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        //Page 를 페이징처리가 된 Model 목록으로 변환해준다.
        //e-> new EventResource(e) > 각 Event를 EventResource 로 변환 작업
        PagedModel pagedResources =   assembler.toModel(page, e-> new EventResource(e));
        //Profile 에 대한 링크 정보만 추가
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity  getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
//        이벤트가 존재하지 않는경우 404 응답
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
//        잘못된 데이터가 넘어온경우 400 응답
        if(errors.hasErrors()){
            return badRequest(errors);
        }
//        비지니스 로직상 맞지 않는경우 400 응답
        this.eventVaildator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
        }
//        성공적으로 수정이 완료된 경우 200 응답
        Event existingEvent = optionalEvent.get();
//        eventDto를 existingEvent로 맵핑해줌
        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);

    }



    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }
}
