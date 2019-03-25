package web.webtest.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import web.webtest.servis.HelloService;

@RestController
@RequestMapping("/hello")
public class HelloResourceController {

  private HelloService helloService; // @InjectMocks ile içi otomatik dolacak

  public HelloResourceController(HelloService helloService) {
    this.helloService = helloService;
  }

  @GetMapping
  public String helloWorld() {
    return helloService.hello();
  }

  @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
  public Hello json() {
    return new Hello("Greetings", "Hello World");
  }

  @PostMapping(
      value = "/post",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Hello post(@RequestBody Hello hello) {
    return hello;
  }

  private static class Hello {
    Hello(){

    }
    private String title;
    private String value;

    public Hello(String title, String value) {
      this.title = title;
      this.value = value;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }
}
