package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class})
class UserDbStorageTest {

	@Autowired
	private UserDbStorage userStorage;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.email("test@email.com")
				.login("testLogin")
				.name("Test Name")
				.birthday(LocalDate.of(1990, 1, 1))
				.build();
	}

	@Test
	void createAndFindUserById() {
		User createdUser = userStorage.create(testUser);
		User foundUser = userStorage.findById(createdUser.getId());

		assertThat(foundUser)
				.isNotNull()
				.usingRecursiveComparison()
				.isEqualTo(createdUser);
	}

	@Test
	void updateUser() {
		User createdUser = userStorage.create(testUser);
		User updatedUser = User.builder()
				.id(createdUser.getId())
				.email("updated@email.com")
				.login("updatedLogin")
				.name("Updated Name")
				.birthday(LocalDate.of(1995, 5, 5))
				.build();

		User result = userStorage.update(updatedUser);

		assertThat(result)
				.isNotNull()
				.usingRecursiveComparison()
				.isEqualTo(updatedUser);
	}

	@Test
	void deleteUser() {
		User createdUser = userStorage.create(testUser);
		Long userId = createdUser.getId();

		Long deletedId = userStorage.delete(userId);

		assertEquals(userId, deletedId);
		assertThrows(ResourceNotFoundException.class, () -> userStorage.findById(userId));
	}

	@Test
	void findAllUsers() {
		userStorage.create(testUser);
		User anotherUser = User.builder()
				.email("another@email.com")
				.login("anotherLogin")
				.name("Another Name")
				.birthday(LocalDate.of(1995, 5, 5))
				.build();
		userStorage.create(anotherUser);

		List<User> users = userStorage.findAll();

		assertThat(users).hasSize(2);
	}

}