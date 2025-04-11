package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Long delete(Long id);

    List<Film> findAll();

    Film findById(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Long> getLikesOfFilm(Long filmId);

    List<Film> getFilmsOfDirectorSortByLikesOrYears(Long id, String sortBy);

    List<Film> findCommonFilms(Long userId, Long friendId);

    List<Film> searchFilmBy(String query, String by);

    List<Film> getMostPopularByGenreYear(Optional<Integer> year, Optional<Long> genreId, Integer limit);
}