package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

  private static final DataStorageController<User> userStorage = new DataStorageController<>();

  @PostMapping
  public User addUser(@RequestBody User user) {
    log.info("Add new User");
    validate(user);
    int id = userStorage.nextId();
    user.setId(id);
    userStorage.addItem(id, user);
    return user;
  }

  @PutMapping
  public User updateUser(@RequestBody User user) {
    log.info("Update User " + user.getId());
    validate(user);
    userStorage.updateItem(user.getId(), user);
    return user;
  }

  @GetMapping
  public Collection<User> getAllUsers() {
    return userStorage.getData();
  }

  void validate(User user) {
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      log.warn("Email is empty");
      throw new ValidationException("Электронная почта не может быть пустой");
    }
    if (!user.getEmail().contains("@")) {
      log.warn("Email @ not found");
      throw new ValidationException("Электронная почта должна содержать символ @");
    }
    if (user.getLogin() == null || user.getLogin().isEmpty())  {
      log.warn("Login is empty");
      throw new ValidationException("Логин не может быть пустым");
    }
    if (user.getLogin().contains(" ")) {
      log.warn("Login has space");
      throw new ValidationException("Логин не может содержать пробелы");
    }
    if ((user.getName() == null) || (user.getName().isBlank())) {
      log.info("Name is empty, using login instead");
      user.setName(user.getLogin());
    }
    if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
      log.warn("Birthday in future");
      throw new ValidationException("Дата рождения не может быть в будущем");
    }
  }

}
