package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    private Film testFilm;

    @BeforeEach
    void setUp() {
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .mpa(new Mpa(1L, "G"))
                .genres(Set.of(new Genre(1L, "Комедия")))
                .build();
    }

    @Test
    void createAndFindFilmById() {
        Film createdFilm = filmStorage.create(testFilm);
        Film foundFilm = filmStorage.findById(createdFilm.getId());

        assertThat(foundFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("genres", "likesUser")
                .isEqualTo(createdFilm);

        assertThat(foundFilm.getGenres()).hasSize(1);
        assertThat(foundFilm.getLikesUser()).isEmpty();
    }

    @Test
    void updateFilm() {
        Film createdFilm = filmStorage.create(testFilm);
        Film updatedFilm = Film.builder()
                .id(createdFilm.getId())
                .name("Updated Film")
                .description("Updated Description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(130)
                .mpa(new Mpa(2L, "PG"))
                .genres(Set.of(new Genre(2L, "Драма")))
                .build();

        Film result = filmStorage.update(updatedFilm);

        assertThat(result)
                .isNotNull()
                .usingRecursiveComparison()
                .ignoringFields("genres", "likesUser")
                .isEqualTo(updatedFilm);

        assertThat(result.getGenres()).hasSize(1);
    }

    @Test
    void deleteFilm() {
        Film createdFilm = filmStorage.create(testFilm);
        Long filmId = createdFilm.getId();

        Long deletedId = filmStorage.delete(filmId);

        assertEquals(filmId, deletedId);
        assertThrows(ResourceNotFoundException.class, () -> filmStorage.findById(filmId));
    }

    @Test
    void findAllFilms() {
        filmStorage.create(testFilm);
        Film anotherFilm = Film.builder()
                .name("Another Film")
                .description("Another Description")
                .releaseDate(LocalDate.of(2001, 1, 1))
                .duration(130)
                .mpa(new Mpa(2L, "PG"))
                .build();
        filmStorage.create(anotherFilm);

        List<Film> films = filmStorage.findAll();

        assertThat(films).hasSize(2);
    }
}