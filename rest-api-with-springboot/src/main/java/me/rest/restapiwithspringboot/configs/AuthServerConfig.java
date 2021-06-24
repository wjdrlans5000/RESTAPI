package me.rest.restapiwithspringboot.configs;


import me.rest.restapiwithspringboot.accounts.AccountService;
import me.rest.restapiwithspringboot.common.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

//@EnableAuthorizationServer 를 사용하여 인증 서버 설정 활성화
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    TokenStore tokenStore;

    @Autowired
    AppProperties appProperties;

    //security 관련 설정
    //clientSecret을 사용할때 passwordEncoder를 사용하기때문에 bean으로 주입받아 사용한다.
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    //client 관련 설정
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //inMemory로 관리한다.
        clients.inMemory()
                //client-id: myApp
                .withClient(appProperties.getClientId())
                .authorizedGrantTypes("password","refresh_token")
                .scopes("read", "write")
                //client-secret: pass
                //passwordEncoder를 사용하여 확인하기 때문에 encoding을 해주어야 한다.
                .secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
                //access-token 유효시간을 10분으로 설정한다.
                .accessTokenValiditySeconds(10 * 60)
                //refresh-token 유효시간을 1시간으로 설정한다.
                .refreshTokenValiditySeconds(6 * 10 * 60);
        ;
    }

    //endpoint 관련 설정
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //사용자 정보를 알고있는 authenticationManager 를 등록한다.
        //이전의 Security 설정(SecurityConfig)에서 authenticationManager 를 빈으로 등록을 해 주었기 때문에 의존성 주입을 받아 사용한다.
        endpoints.authenticationManager(authenticationManager)
                //userDetailsService로 accountService를 사용
                .userDetailsService(accountService)
                //마찬가지로 이전의 security 설정(SecurityConfig)에서 빈으로 등록을 해 주었기 때문에 의존성 주입을 받아 사용한다.
                .tokenStore(tokenStore);
    }
}
