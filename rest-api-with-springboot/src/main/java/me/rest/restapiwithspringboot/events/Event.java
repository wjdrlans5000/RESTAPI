package me.rest.restapiwithspringboot.events;

import lombok.*;

import java.time.LocalDateTime;

/*
빌더로 생성하면 기본 생성자가 생성되지 않고 모든 파라미터를 가지고 있는 생성자가 생성됨.
public이 아닌 default로 생성됨(다른패카지에서 이 이벤트에 대한 객체를 만들기가 애매함
@AllArgsConstructor @NoArgsConstructor 기본생성자와 모든 아규먼트를 가진 생성자 둘다 만들기위해
@EqualsAndHashCode(of = "id") > 이퀄스와 해쉬코드를 구현할때 아래 모든 필드를 다 사용을 함.
그런데 나중에 엔티티간에 연관관계가 상호 참조하는 관계가 되면 스택오버플로우가 발생할수 있어서
id의 값만가지고 이퀄스와 해쉬코드 값을 비교하도록 사용.
- Lombok 애노테이션은 meta 애노테이션으로 동작하지 않음.
*/
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Event {

    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    private EventStatus eventStatus;

}
