package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.Identifiable;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film implements Identifiable {

  private int id;

  private String name;

  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate releaseDate;

  private Integer duration;

}
