package me.rest.restapiwithspringboot.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException;

    @Autowired
    AccountService accountService;

//    @Autowired
//    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername(){
        //Given
        String password = "gimun";
        String userName = "gimun@mail.com";
        Account account = Account.builder()
                .email(userName)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build()
                ;

        //accountRepository의 save를 직접사용하지않고 accountService에 정의해둔 패스워드 인코더를 사용한 메서드 사용
        this.accountService.saveAccount(account);

//        this.accountRepository.save(account);

        //When
        UserDetailsService userDetailsService = (UserDetailsService) accountService;
        //username(email) 에 해당하는 사용자를 찾는다.
        //없다면 UsernameNotFoundException
        //존재한다면 UserDetail 구현체인 User 객체를 반환한다.
        UserDetails userDetails =  userDetailsService.loadUserByUsername(userName);

        //Then
        //입력한 패스워드와 인코딩되어 save된 패스워드가 일치하는지 확인
        assertThat(this.passwordEncoder.matches(
                password,
                userDetails.getPassword()
        )).isTrue();
//        assertThat(userDetails.getPassword()).isEqualTo(password)

        ;
    }

//    다양한 예외 테스트하는 방법.
//    1. @Test(expected = UsernameNotFoundException.class)
    @Test
    public void findByUsernameFail(){
        String username = "random@email.com";

        //2.
        //훨씬 많은걸 테스트할수 있음
//        try {
//            accountService.loadUserByUsername(username);
//            fail("supposed to be failed");
//        }catch (UsernameNotFoundException e){
//            assertThat(e.getMessage()).containsSequence(username);
//        }

        //3. 발생할(예측되는) 예외를 미리 적어줘야함
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(Matchers.containsString(username));

        // When
        accountService.loadUserByUsername(username);



    }

}