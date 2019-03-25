package web.webtest.servis;

import org.springframework.stereotype.Component;
import web.webtest.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class HelloService {
  private static List<User> users =
      new ArrayList<User>(
          Arrays.asList(
              new User(1, "Daenerys Targaryen"),
              new User(2, "John Snow"),
              new User(3, "Arya Stark"),
              new User(4, "Cersei Baratheon")));

  public String hello() {
    return "Hello World";
  }

  public List<User> getAll() {

    return users;
  }

  public User findById(int id) {
    for (User user : users) {
      if (user.getId() == id) {
        return user;
      }
    }
    return null;
  }

  public boolean exists(User user) {
    return findByName(user.getUsername()) != null;
  }

  public User findByName(String name) {
    for (User user : users) {
      if (user.getUsername().equals(name)) {
        return user;
      }
    }
    return null;
  }

  public void create(User user) {
    Random random = new Random();
    user.setId(random.nextInt() + 19);
    users.add(user);
  }

  public void update(User user) {
    int index = users.indexOf(findById(user.getId()));
    users.set(index, user);
  }

  public void delete(int id) {
    User user = findById(id);
    users.remove(user);
  }
}
