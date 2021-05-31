package me.rest.restapiwithspringboot.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventVaildator {

    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() > 0){
//            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong.");
//            errors.rejectValue("maxPrice","wrongValue","MaxPrice is wrong.");
            //글로벌에러
            errors.reject("wrongPrices","Values to Prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
                endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
                endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            //필드에러
            errors.rejectValue("endEventDateTime","wrongValue","endEventDateTime is wrong");
        }

        // TODO BeginEventDateTime
        // TODO CloseEnrollmentDateTime
    }
}
