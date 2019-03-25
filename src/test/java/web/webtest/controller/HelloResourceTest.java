package web.webtest.controller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import web.webtest.servis.HelloService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest

public class HelloResourceTest {
  @Mock HelloService helloService; // bu when then için dönen sonuc

  private MockMvc mockMvc;

  @InjectMocks
  private HelloResourceController helloResourceController; // Controlller

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(helloResourceController).build();
  }

  @Test
  public void testHelloWorld() throws Exception {
    Mockito.when(helloService.hello()).thenReturn("hello");

    mockMvc
        .perform(MockMvcRequestBuilders.get("/hello"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().string("hello"));

    Mockito.verify(helloService).hello();
  }

  @Test
  public void testHelloWorldJson() throws Exception {
    mockMvc
        .perform(get("/hello/json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", Matchers.is("Greetings")))
        .andExpect(jsonPath("$.value", Matchers.is("Hello World")))
        .andExpect(jsonPath("$.*", Matchers.hasSize(2)));
  }

  @Test
  public void testPost() throws Exception {
    String json = "{\n" + "  \"title\": \"Greetings\",\n" + "  \"value\": \"Hello World\"\n" + "}";
    mockMvc
        .perform(
            post("/hello/post")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(json)) // içini doldurk json veri ike
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title", Matchers.is("Greetings"))) // bize dönen değer
        .andExpect(jsonPath("$.value", Matchers.is("Hello World")))
        .andExpect(jsonPath("$.*", Matchers.hasSize(2)));
  }
}
