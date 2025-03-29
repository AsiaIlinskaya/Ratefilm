package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.Identifiable;

import java.time.LocalDate;

/**
 * User.
 */
@Data
public class User implements Identifiable {

  private int id;

  private String email;

  private String login;

  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;

}
