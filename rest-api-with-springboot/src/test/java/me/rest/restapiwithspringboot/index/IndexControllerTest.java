package me.rest.restapiwithspringboot.index;

import me.rest.restapiwithspringboot.common.RestDocsConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc // 모킹을 사용하지않고 실제 리파지토리를 사용하여 테스트 동작
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class) //다른 스프링 bean 설정파일을 읽어와서 사용하는 방법 중 하나
@ActiveProfiles("test") //test application properties를 추가로 사용
public class IndexControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/api/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists())

        ;
    }
}
