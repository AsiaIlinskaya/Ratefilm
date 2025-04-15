package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class})
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreStorage;

    @Test
    void findGenreById() {
        Genre genre = genreStorage.findById(1L);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void findNonExistentGenreById() {
        assertThrows(ResourceNotFoundException.class, () -> genreStorage.findById(999L));
    }

    @Test
    void findAllGenres() {
        Map<Long, Genre> genres = genreStorage.findAll();

        assertThat(genres).hasSize(6);
        assertThat(genres.get(1L).getName()).isEqualTo("Комедия");
        assertThat(genres.get(2L).getName()).isEqualTo("Драма");
    }
}