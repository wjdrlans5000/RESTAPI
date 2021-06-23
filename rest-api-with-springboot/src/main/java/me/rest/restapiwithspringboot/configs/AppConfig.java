package me.rest.restapiwithspringboot.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //prifix를 붙여 어떠한 방식으로 인코딩 된건지 알수있도록 해줌.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
