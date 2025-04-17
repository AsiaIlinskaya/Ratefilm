package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Long delete(Long id);

    List<Film> findAll();

    Film findById(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getMostPopular(Integer limit);

    Map<Long, Set<Genre>> getGenresByFilmIds(List<Long> filmIds);

    Map<Long, Set<Long>> getLikesByFilmIds(List<Long> filmIds);
}