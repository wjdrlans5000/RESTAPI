package me.rest.restapiwithspringboot.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rest.restapiwithspringboot.common.RestDocsConfiguration;
import me.rest.restapiwithspringboot.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
* @WebMvcTest
* MockMvc 빈을 자동 설정 해준다. 따라서 그냥 가져와서 쓰면 됨.
* 웹 관련 빈만 등록해 준다. (슬라이스)
* 리파지토리 빈으로 등록해주지 않음.
* */
@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc // 모킹을 사용하지않고 실제 리파지토리를 사용하여 테스트 동작
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) //다른 스프링 bean 설정파일을 읽어와서 사용하는 방법 중 하나
@ActiveProfiles("test")
public class EventControllerTests {

    /*
    * MockMvc는 요청을만들고 응답을 검증할수있는 스프링MVC 테스트에 있어서 핵심적인 클래스 중 하나.
    * 웹 서버를 띄우지 않고도 스프링 MVC (DispatcherServlet)가 요청을 처리하는 과정을 확인할 수 있기 때문에 컨트롤러 테스트용으로 자주 쓰임.
    * 디스패처서블릿을 만들어야하기때문에 단위테스트보다는 느림.
    * */
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    //리파지토리를 목킹
//    @MockBean
//    EventRepository eventRepository;


    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {

        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,5,25,23,03))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,5,26,23,03))
                .beginEventDateTime(LocalDateTime.of(2021,5,25,23,03))
                .endEventDateTime(LocalDateTime.of(2021,5,26,23,03))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        /*
         * perform 안에 post 요청을 줌
         * */
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print()) //  Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인.
                .andExpect(status().isCreated()) // 해당 요청의 응답으로 isCreated (201) 을 만족하는지 확인.
                .andExpect(jsonPath("id").exists()) //  id는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인
                .andExpect(header().exists("Location"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                //입력값 제한하기
//                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT)))
//                self: 리소스 에 대한 링크
//                query-events: 이벤트목록에 대한 링크
//                update-event: 이벤트 수정에 대한 링크
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                //REST Docs 사용
                .andDo(document("create-event"
                        ,links(
                                linkWithRel("self").description("link to self")
                                ,linkWithRel("query-events").description("link to query events")
                                ,linkWithRel("update-event").description("link to update and existing")
                                ,linkWithRel("profile").description("link to profile")
                        )
                        ,requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                                ,headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        )
                        ,requestFields(
                                fieldWithPath("name").description("Name of new event")
                                ,fieldWithPath("description").description("description of new event")
                                ,fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                ,fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                ,fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                ,fieldWithPath("endEventDateTime").description("date time of end of new event")
                                ,fieldWithPath("location").description("location of new event")
                                ,fieldWithPath("basePrice").description("basePrice of new event")
                                ,fieldWithPath("maxPrice").description("maxPrice of new event")
                                ,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        )
                        ,responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header")
                                ,headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
                        )
                        //문서의 일부분만 확인 -> 단점 : 정확한 문서를 만들지 못함.
//                        ,relaxedResponseFields(
                        ,responseFields(
                                fieldWithPath("id").description("id of new event")
                                , fieldWithPath("name").description("Name of new event")
                                ,fieldWithPath("description").description("description of new event")
                                ,fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event")
                                ,fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event")
                                ,fieldWithPath("beginEventDateTime").description("date time of begin of new event")
                                ,fieldWithPath("endEventDateTime").description("date time of end of new event")
                                ,fieldWithPath("location").description("location of new event")
                                ,fieldWithPath("basePrice").description("basePrice of new event")
                                ,fieldWithPath("maxPrice").description("maxPrice of new event")
                                ,fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                                ,fieldWithPath("free").description("it tells is this event is free or not")
                                ,fieldWithPath("offline").description("it tells is this event is offline or not")
                                ,fieldWithPath("eventStatus").description("event status")
                                ,fieldWithPath("_links.self.href").description("link to self")
                                ,fieldWithPath("_links.query-events.href").description("link to query events list")
                                ,fieldWithPath("_links.update-event.href").description("link to update existing event")
                                ,fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))

        ;


    }

    @Test
    @TestDescription("입력값이 받을수 없는  값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {

        Event event = Event.builder()
                .id(100) //db들어갈때 자동생성되야함
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2021,5,25,23,03))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,5,26,23,03))
                .beginEventDateTime(LocalDateTime.of(2021,5,25,23,03))
                .endEventDateTime(LocalDateTime.of(2021,5,26,23,03))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true) //위 값들이 있는 경우엔  free가 아님
                .offline(false) // 위 값이 있을 경우엔 true
                .eventStatus(EventStatus.PUBLISHED)
                .build();
        //eventRepository의 세이브가 호출이 될때 event 오브젝트를 받았을때 이벤트를 리턴하라
        //모킹을 했을때 save에 전달한 객체가 modelMapper로  새로 만든 객체이기에 위의 save와 같은 객체가 아니라서 널포인트 이셉션 발생
//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        /*
         * perform 안에 post 요청을 줌
         * */
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print()) //  Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인.
                .andExpect(status().isBadRequest()) //unknown 프로퍼티들(EventDTO가 아닌 프로퍼티들)을 넘기고 있으면 400 bad request가 떨어짐
        ;
    }

//    아무런 입력값도 받지 않을경우 BAD_REQUEST를 받는 테스트코드
    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        //입력값이 없기때문에 베드리퀘스트가 나와야함 (201이나오면 안됨.)
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    //입력값이 들어오지만  이상한경우 테스트코드
    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        //입력값이 없기때문에 베드리퀘스트가 나와야함 (201이나오면 안됨.)
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                //이벤트 끝나는 날짜가 시작날짜보다 빠름 -> 즉 값이 잘못됨.
                .beginEnrollmentDateTime(LocalDateTime.of(2021,5,25,23,03))
                .closeEnrollmentDateTime(LocalDateTime.of(2021,5,26,23,03))
                .beginEventDateTime(LocalDateTime.of(2021,5,25,23,03))
                .endEventDateTime(LocalDateTime.of(2021,5,26,23,03))
                //베이스가 10000인데 맥스가 200??? -> 값이 잘못됨.
                //애노테이션으로 검증하기가 어려움.
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        // 시리얼라이저에서 필드에러를 먼저 만들었기떄문에 필드에러가 없는경우에 깨짐
        // 원래는 더 꼼꼼히 테스트 작성해야함.
//                .andExpect(jsonPath("$[0].field").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
        ;
    }

}
