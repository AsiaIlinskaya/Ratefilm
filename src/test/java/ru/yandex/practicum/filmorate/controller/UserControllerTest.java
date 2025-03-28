package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

  private final UserController userController = new UserController();

  @Test
  void validateUser_Valid() {
    User user = new User();
    user.setLogin("TestLogin");
    user.setName("Test name");
    user.setEmail("email@test.tst");
    user.setBirthday(LocalDate.of(2000, 6, 10));
    assertDoesNotThrow(() -> userController.validate(user));
  }

  @Test
  void validateUser_InvalidEmail() {
    User user = new User();
    user.setLogin("TestLogin");
    user.setName("Test name");
    user.setBirthday(LocalDate.of(2000, 6, 10));
    assertThrows(ValidationException.class, () -> userController.validate(user));
    user.setEmail("");
    assertThrows(ValidationException.class, () -> userController.validate(user));
    user.setEmail("invalidmail");
    assertThrows(ValidationException.class, () -> userController.validate(user));
  }

  @Test
  void validateUser_InvalidLogin() {
    User user = new User();
    user.setName("Test name");
    user.setEmail("email@test.tst");
    user.setBirthday(LocalDate.of(2000, 6, 10));
    assertThrows(ValidationException.class, () -> userController.validate(user));
    user.setLogin("");
    assertThrows(ValidationException.class, () -> userController.validate(user));
    user.setLogin("Test login");
    assertThrows(ValidationException.class, () -> userController.validate(user));
  }

  @Test
  void validateUser_InvalidBirthday() {
    User user = new User();
    user.setLogin("TestLogin");
    user.setName("Test name");
    user.setEmail("email@test.tst");
    assertThrows(ValidationException.class, () -> userController.validate(user));
    user.setBirthday(LocalDate.now());
    assertDoesNotThrow(() -> userController.validate(user));
    user.setBirthday(LocalDate.now().plusDays(1));
    assertThrows(ValidationException.class, () -> userController.validate(user));
  }

  @Test
  void validateUser_NullObject() {
    assertThrows(IllegalArgumentException.class, () -> userController.validate(null));
  }

}