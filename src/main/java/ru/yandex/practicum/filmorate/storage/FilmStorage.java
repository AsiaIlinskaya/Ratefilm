package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Long delete(Long id);

    List<Film> findAll();

    Film findById(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Long> getLikesOfFilm(Long filmId);

    List<Film> getMostPopular(Integer limit);
}