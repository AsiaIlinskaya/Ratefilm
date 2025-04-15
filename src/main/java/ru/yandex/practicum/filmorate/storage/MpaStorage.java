package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Map;

public interface MpaStorage {
    Mpa findById(Long id);

    Map<Long, Mpa> findAll();
}
