package me.rest.restapiwithspringboot.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

import java.io.IOException;

//에러시리얼라이저를 오브젝트 매퍼에 등록
@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("errors");
        jsonGenerator.writeStartArray();
        //Field Errors
        errors.getFieldErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("field",e.getField());
                jsonGenerator.writeStringField("objectName",e.getObjectName());
                jsonGenerator.writeStringField("code",e.getCode());
                jsonGenerator.writeStringField("defaultMessage",e.getDefaultMessage());
                Object rejectedValue = e.getRejectedValue();
                if(rejectedValue != null){
                    jsonGenerator.writeStringField("rejectedValue",rejectedValue.toString());
                }
                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        //Globals Errors
        errors.getGlobalErrors().forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("objectName",e.getObjectName());
                jsonGenerator.writeStringField("code",e.getCode());
                jsonGenerator.writeStringField("defaultMessage",e.getDefaultMessage());
                jsonGenerator.writeEndObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        jsonGenerator.writeEndArray();
    }
}
