package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        log.debug("Запрос на получение всех рейтингов MPA");
        Collection<Mpa> mpaRatings = Collections.unmodifiableCollection(mpaStorage.findAll().values());
        log.debug("Получено {} рейтингов MPA", mpaRatings.size());
        return mpaRatings;
    }

    public Mpa findById(Long id) {
        log.debug("Запрос на получение рейтинга MPA с id {}", id);
        Mpa mpa = mpaStorage.findById(id);
        log.debug("Найден рейтинг MPA: {}", mpa);
        return mpa;
    }
}