package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

  private final FilmController filmController = new FilmController();

  @Test
  void validateFilm_Valid() {
    Film film = new Film();
    film.setName("Test name");
    film.setDescription("Test description");
    film.setReleaseDate(LocalDate.of(1980, 11, 17));
    film.setDuration(7200);
    assertDoesNotThrow(() -> filmController.validate(film));
  }

  @Test
  void validateFilm_InvalidName() {
    Film film = new Film();
    film.setDescription("Test description");
    film.setReleaseDate(LocalDate.of(1980, 11, 17));
    film.setDuration(7200);
    assertThrows(ValidationException.class, () -> filmController.validate(film));
    film.setName("");
    assertThrows(ValidationException.class, () -> filmController.validate(film));
  }

  @Test
  void validateFilm_InvalidDescription() {
    Film film = new Film();
    film.setName("Test name");
    film.setReleaseDate(LocalDate.of(1980, 11, 17));
    film.setDuration(7200);
    assertDoesNotThrow(() -> filmController.validate(film));
    film.setDescription("""
                        Alien is a 1979 science fiction horror film directed by Ridley Scott
                         and written by Dan O'Bannon, based on a story by O'Bannon and Ronald
                         Shusett. It follows a spaceship crew who investigate a derelict spaceship
                         and are hunted by a deadly extraterrestrial creature. The film stars Tom
                         Skerritt, Sigourney Weaver, Veronica Cartwright, Harry Dean Stanton,
                         John Hurt, Ian Holm, and Yaphet Kotto. It was produced by Gordon Carroll,
                         David Giler, and Walter Hill through their company Brandywine Productions
                         and was distributed by 20th Century-Fox. Giler and Hill revised and made
                         additions to the script; Shusett was the executive producer. The alien
                         creatures and environments were designed by the Swiss artist H. R. Giger,
                         while the concept artists Ron Cobb and Chris Foss designed the other sets.
                        """);
    assertThrows(ValidationException.class, () -> filmController.validate(film));
  }

  @Test
  void validateFilm_InvalidReleaseDate() {
    Film film = new Film();
    film.setName("Test name");
    film.setDescription("Test description");
    film.setDuration(7200);
    assertThrows(ValidationException.class, () -> filmController.validate(film));
    film.setReleaseDate(LocalDate.of(1895, 12, 28));
    assertDoesNotThrow(() -> filmController.validate(film));
    film.setReleaseDate(LocalDate.of(1895, 12, 27));
    assertThrows(ValidationException.class, () -> filmController.validate(film));
  }

  @Test
  void validateFilm_InvalidDuration() {
    Film film = new Film();
    film.setName("Test name");
    film.setDescription("Test description");
    film.setReleaseDate(LocalDate.of(1980, 11, 17));
    assertThrows(ValidationException.class, () -> filmController.validate(film));
    film.setDuration(0);
    assertThrows(ValidationException.class, () -> filmController.validate(film));
    film.setDuration(-1);
    assertThrows(ValidationException.class, () -> filmController.validate(film));
  }

}