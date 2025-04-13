package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Film create(Film film) {
        film.setId(idCounter.getAndIncrement());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        findById(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Long delete(Long id) {
        findById(id);
        films.remove(id);
        return id;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new ResourceNotFoundException(String.format("Фильм с id %d не найден", id));
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        film.getLikesUser().add(userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Film film = findById(filmId);
        film.getLikesUser().remove(userId);
    }

    @Override
    public List<Long> getLikesOfFilm(Long filmId) {
        return new ArrayList<>(findById(filmId).getLikesUser());
    }

    @Override
    public List<Film> getMostPopular(Integer limit) {
        return films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesUser().size(), f1.getLikesUser().size()))
                .limit(limit)
                .toList();
    }
}