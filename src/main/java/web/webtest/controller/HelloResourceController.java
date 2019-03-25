package web.webtest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import web.webtest.model.User;
import web.webtest.servis.HelloService;

import java.util.List;

@RestController
@RequestMapping("/hello")
public class HelloResourceController {

  @Autowired private HelloService helloService; // @InjectMocks ile içi otomatik dolacak

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

  @GetMapping("/users")
  public List<User> getAll() {
    return helloService.getAll();
  }

  // =========================================== Get User By ID
  // =========================================

  @GetMapping("/users/{id}")
  public ResponseEntity<User> get(@PathVariable int id) {
    User user = helloService.findById(id);

    if (user == null) {
      return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<User>(user, HttpStatus.OK);
  }
  // =========================================== Create New User
  // ========================================

  @PostMapping("/users")
  public ResponseEntity<Void> create(@RequestBody User user, UriComponentsBuilder ucBuilder) {

    if (helloService.exists(user)) {
      return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    }

    helloService.create(user);

    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(ucBuilder.path("/users/{id}").buildAndExpand(user.getId()).toUri());
    return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
  }
  // =========================================== Update Existing User
  // ===================================

  @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
  public ResponseEntity<User> update(@PathVariable int id, @RequestBody User user) {
    User currentUser = helloService.findById(id);

    if (currentUser == null) {
      return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    }

    currentUser.setId(user.getId());
    currentUser.setUsername(user.getUsername());

    helloService.update(user);
    return new ResponseEntity<User>(currentUser, HttpStatus.OK);
  }

  @DeleteMapping(value = "/users/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") int id){
    User user = helloService.findById(id);

    if (user == null){
      return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    helloService.delete(id);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }


  private static class Hello {
    private String title;
    private String value;

    Hello() {}

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
