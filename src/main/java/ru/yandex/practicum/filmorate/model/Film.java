package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {

  private int id;

  private String name;

  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate releaseDate;

  private Integer duration;

}
