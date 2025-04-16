package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;


    public Film create(Film film) {
        mpaStorage.findById(film.getMpa().getId());
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Long removeById(Long id) {
        return filmStorage.delete(id);
    }

    public List<Film> findAll() {
        List<Film> films = filmStorage.findAll();
        enrichFilmsWithData(films);
        return films;
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film addLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.addLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.deleteLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public List<Film> getMostPopular(Integer limit) {
        List<Film> films = filmStorage.getMostPopular(limit);
        enrichFilmsWithData(films);
        return films;
    }

    private void enrichFilmsWithData(List<Film> films) {
        if (films.isEmpty()) {
            return;
        }

        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Set<Genre>> genresByFilmId = filmStorage.getGenresByFilmIds(filmIds);
        Map<Long, Set<Long>> likesByFilmId = filmStorage.getLikesByFilmIds(filmIds);

        films.forEach(film -> {
            Set<Genre> genres = genresByFilmId.getOrDefault(film.getId(), Collections.emptySet())
                    .stream()
                    .sorted(Comparator.comparingLong(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(genres);

            Set<Long> likes = likesByFilmId.getOrDefault(film.getId(), Collections.emptySet());
            film.setLikesUser(likes);
        });
    }
}