package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

  private static final DataStorageController<Film> filmStorage = new DataStorageController<>();

  @PostMapping
  public Film addFilm(@RequestBody Film film) {
    log.info("Add new Film");
    validate(film);
    int id = filmStorage.nextId();
    film.setId(id);
    filmStorage.addItem(id, film);
    return film;
  }

  @PutMapping
  public Film updateFilm(@RequestBody Film film) {
    log.info("Update Film " + film.getId());
    validate(film);
    filmStorage.updateItem(film.getId(), film);
    return film;
  }

  @GetMapping
  public Collection<Film> getAllFilms() {
    return filmStorage.getData();
  }

  void validate(Film film) {
    if ((film.getName() == null) || (film.getName().trim().isEmpty())) {
      log.warn("Name is empty");
      throw new ValidationException("Название не может быть пустым");
    }
    if ((film.getDescription() != null) && (film.getDescription().length() > 200)) {
      log.warn("Description too long");
      throw new ValidationException("Максимальная длина описания — 200 символов");
    }
    LocalDate startDate = LocalDate.of(1895, 12, 28);
    if ((film.getReleaseDate() != null) && (film.getReleaseDate().isBefore(startDate))) {
      log.warn("Release date too early");
      throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
    }
    if ((film.getDuration() != null) && !(film.getDuration() > 0)) {
      log.warn("Duration is not positive");
      throw new ValidationException("Продолжительность фильма должна быть положительным числом");
    }
  }

}
