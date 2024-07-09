package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByEmail() {
        //given
        User user = createUser("ivanov@gmail.com", "Ivan");

        //that
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.get().getName()).isEqualTo(user.getName());
    }

    @Test
    void testSaveUser() {
        //given that
        User newUser = createUser("petrov@gmail.com", "Petr");

        //then
        assertThat(newUser).isNotNull();
        assertThat(newUser.getId()).isNotNull();
        assertThat(newUser.getEmail()).isEqualTo("petrov@gmail.com");
        assertThat(newUser.getName()).isEqualTo("Petr");
    }

    @Test
    void testDeleteUser() {
        //given
        User user = createUser("ivanov@gmail.com", "Ivan");

        //that
        userRepository.delete(user);

        //then
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertThat(deletedUser).isNotPresent();
    }

    @Test
    void testFindById() {
        //given
        User user = createUser("ivanov@gmail.com", "Ivan");

        //that
        Optional<User> foundUser = userRepository.findById(user.getId());

        //then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user.getId());
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

}
