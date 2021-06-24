package me.rest.restapiwithspringboot.configs;

import me.rest.restapiwithspringboot.accounts.Account;
import me.rest.restapiwithspringboot.accounts.AccountRole;
import me.rest.restapiwithspringboot.accounts.AccountService;
import me.rest.restapiwithspringboot.common.BaseControllerTest;
import me.rest.restapiwithspringboot.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class AuthServerConfigTest extends BaseControllerTest {
        
    @Autowired
    AccountService accountService;
    
    @Test
    @TestDescription("인증 토큰을 발급 받는 테스트")
    public void getAuthToken() throws Exception{
        //given
        String username = "gimunAccount gimun = @email.com";
        String password = "gimun";
        Account gimun = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.accountService.saveAccount(gimun);

        String clientId  = "myApp";
        String clientSecret = "pass";

        //        - POST /oauth/token 으로 요청을 보내면 access_token 이 발급 되기를 기대한다.
        this.mockMvc.perform(post("/oauth/token")
                //        - 요청 HEADER에 httpBasic() 을 사용하여 basicOauth HEADER를 만들어 요청에 같이 보낸다.
                .with(httpBasic(clientId,clientSecret))
                //        - 요청 Parameter로 grant_type, username, password 을 전달한다.
                .param("username",username)
                .param("password",password)
                .param("grant_type","password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())

        ;
    }
    
}