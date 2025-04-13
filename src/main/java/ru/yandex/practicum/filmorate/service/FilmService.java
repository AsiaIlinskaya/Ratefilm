package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        filmStorage.findById(film.getId());
        return filmStorage.update(film);
    }

    public Long removeById(Long id) {
        return filmStorage.delete(id);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film addLike(Long filmId, Long userId) {
        userStorage.findById(userId);
        filmStorage.addLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        userStorage.findById(userId);
        filmStorage.deleteLike(filmId, userId);
        return filmStorage.findById(filmId);
    }

    public List<Film> getMostPopular(Integer limit) {
        return filmStorage.getMostPopular(limit);
    }
}