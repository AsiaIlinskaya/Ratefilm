package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        log.debug("Запрос на получение всех жанров");
        Collection<Genre> genres = Collections.unmodifiableCollection(genreStorage.findAll().values());
        log.debug("Получено {} жанров", genres.size());
        return genres;
    }

    public Genre findById(Long id) {
        log.debug("Запрос на получение жанра с id {}", id);
        Genre genre = genreStorage.findById(id);
        log.debug("Найден жанр: {}", genre);
        return genre;
    }
}