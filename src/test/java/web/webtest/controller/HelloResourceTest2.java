package web.webtest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import web.webtest.model.User;
import web.webtest.servis.HelloService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class HelloResourceTest2 {
  @Mock HelloService helloService; // bu when then için dönen sonuc

  private MockMvc mockMvc;

  @InjectMocks private HelloResourceController helloResourceController; // Controlller

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

  @Test
  public void testPost2() throws Exception {
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

  // =========================================== Get All Users
  // ==========================================

  @Test
  public void test_get_all_success() throws Exception {
    List<User> users = Arrays.asList(new User(1, "Daenerys Targaryen"), new User(2, "John Snow"));
    when(helloService.getAll()).thenReturn(users);

    mockMvc
        .perform(get("/hello/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].username", is("Daenerys Targaryen")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].username", is("John Snow")));

    System.out.println("aa" + helloService.getAll());
    // verify(helloService, times(1)).getAll();
    // verifyNoMoreInteractions(helloService);
  }
  // =========================================== Get User By ID
  // =========================================

  @Test
  public void test_get_by_id_success() throws Exception {
    User user = new User(1, "Daenerys Targaryen");
    when(helloService.findById(1)).thenReturn(user);

    mockMvc.perform(get("/hello/users/{id}",1))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            . andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.username", is("Daenerys Targaryen")));
    verify(helloService,times(1)).findById(1);
    verifyNoMoreInteractions(helloService);
  }


  @Test
  public void test_get_by_id_fail_404_not_found() throws Exception {
    when(helloService.findById(2)).thenReturn(null);

    mockMvc.perform(get("/users/{id}", 2))
            .andExpect(status().isNotFound());

    //verify(helloService, times(2)).findById(2);
    verifyNoMoreInteractions(helloService);
  }

  // =========================================== Create New User ========================================

  @Test
  public void test_create_user_success() throws Exception {
    User user=new User("Arya Stark");
    when(helloService.exists(user)).thenReturn(false);
    doNothing().when(helloService).create(user);
    mockMvc.perform(
            post("/hello/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
            .andExpect(status().isCreated())
            .andExpect(header().string("location", containsString("/users/0")));

    verify(helloService, times(1)).exists(user);
    verify(helloService, times(1)).create(user);
    verifyNoMoreInteractions(helloService);
  }




  // =========================================== Update Existing User ===================================
  @Test
  public void test_update_user_success() throws Exception {
    User user = new User(1, "Arya Stark");

    when(helloService.findById(user.getId())).thenReturn(user);
    doNothing().when(helloService).update(user);

    mockMvc.perform(
            put("/hello/users/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
            .andExpect(status().isOk());

    verify(helloService, times(1)).findById(user.getId());
    verify(helloService, times(1)).update(user);
    verifyNoMoreInteractions(helloService);
  }

  @Test
  public void test_update_user_fail_404_not_found() throws Exception {
    User user = new User(999, "user not found");

    when(helloService.findById(user.getId())).thenReturn(null);

    mockMvc.perform(
            put("/hello/users/{id}", user.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(user)))
            .andExpect(status().isNotFound());

    verify(helloService, times(1)).findById(user.getId());
    verifyNoMoreInteractions(helloService);
  }

  // =========================================== Delete User ============================================

  @Test
  public void test_delete_user_success() throws Exception {
    User user = new User(1, "Arya Stark");

    when(helloService.findById(user.getId())).thenReturn(user);
    doNothing().when(helloService).delete(user.getId());

    mockMvc.perform(
            delete("/hello/users/{id}", user.getId()))
            .andExpect(status().isOk());

    verify(helloService, times(1)).findById(user.getId());
    verify(helloService, times(1)).delete(user.getId());
    verifyNoMoreInteractions(helloService);
  }

  @Test
  public void test_delete_user_fail_404_not_found() throws Exception {
    User user = new User(999, "user not found");

    when(helloService.findById(user.getId())).thenReturn(null);

    mockMvc.perform(
            delete("/hello/users/{id}", user.getId()))
            .andExpect(status().isNotFound());

    verify(helloService, times(1)).findById(user.getId());
    verifyNoMoreInteractions(helloService);
  }


  public static String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
