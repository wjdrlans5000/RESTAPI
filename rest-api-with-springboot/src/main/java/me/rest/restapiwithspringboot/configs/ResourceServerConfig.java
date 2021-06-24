package me.rest.restapiwithspringboot.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("event");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //        - 익명사용자를 허용한다.
        http.anonymous()
                .and()
                .authorizeRequests()
                //         - GET /api/** 의 모든 요청들은 접근을 허용하도록 한다.
                .mvcMatchers(HttpMethod.GET,"/api/**").anonymous()
                //         - 나머지 요청에 대해서는 인증을 진행한다.
                .anyRequest()
                .authenticated()
                .and()
                //인증이 잘못되거나 권한이 없는경우 예외가 발생할수있는데 접근권한이 없는 경우 OAuth2AccessDeniedHandler 를 사용하도록 설정
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
