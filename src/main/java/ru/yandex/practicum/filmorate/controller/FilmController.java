package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{filmId}")
    public void removeById(@PathVariable Long filmId) {
        filmService.removeById(filmId);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Long id) {
        return filmService.findById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Long filmId,
                        @PathVariable("userId") Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film removeLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsOfDirectorSortByLikesOrYears(@PathVariable("directorId") Long id,
                                                           @RequestParam(defaultValue = "likes") String sortBy) {
        return filmService.getFilmsOfDirectorSortByLikesOrYears(id, sortBy);
    }

    @GetMapping("/common")
    public List<Film> findCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return filmService.findCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> getSearchFilmsBy(
            @RequestParam String query,
            @RequestParam String by) {
        return filmService.searchFilmBy(query, by);
    }

    @GetMapping("/popular")
    public List<Film> getMostPopularByGenreYear(@RequestParam Optional<Integer> year,
                                                @RequestParam Optional<Long> genreId,
                                                @RequestParam(defaultValue = "10") @Positive Integer count) {
        return filmService.getMostPopularByGenreYear(year, genreId, count);
    }
}