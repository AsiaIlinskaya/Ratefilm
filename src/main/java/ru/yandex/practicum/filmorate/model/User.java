package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@SuperBuilder
@NoArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    @Email(message = "электронная почта не может быть пустой и должна содержать символ @")
    private String email;
    @NotBlank
    @Pattern(regexp = "^\\S+$", message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    @Nullable
    private String name;
    @NotNull
    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
}