package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaDbStorage.class})
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaStorage;

    @Test
    void findMpaById() {
        Mpa mpa = mpaStorage.findById(1L);

        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1L);
        assertThat(mpa.getName()).isEqualTo("G");
    }

    @Test
    void findNonExistentMpaById() {
        assertThrows(ResourceNotFoundException.class, () -> mpaStorage.findById(999L));
    }

    @Test
    void findAllMpa() {
        Map<Long, Mpa> mpaRatings = mpaStorage.findAll();

        assertThat(mpaRatings).hasSize(5);
        assertThat(mpaRatings.get(1L).getName()).isEqualTo("G");
        assertThat(mpaRatings.get(2L).getName()).isEqualTo("PG");
    }
}