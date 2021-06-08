package me.rest.restapiwithspringboot.common;

import me.rest.restapiwithspringboot.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsResource extends EntityModel<Errors> {
    public ErrorsResource(Errors content, Link... links) {
        super(content, links);
        //리소스 변환시 index 링크 정보 추가
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
