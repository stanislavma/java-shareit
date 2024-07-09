package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.Request;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindAllByRequestUserId() {
        //given
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        User requestorUser = createUser("requestor_user@gmail.com", "Test Requestor User");
        createRequest(requestorUser, "Test Request");

        //that
        List<Request> requests = requestRepository.findAllByRequestUserId(requestorUser.getId(), sort);

        //then
        assertThat(requests).hasSize(1);
        Request foundRequest = requests.get(0);
        assertThat(foundRequest.getDescription()).isEqualTo("Test Request");
        assertThat(foundRequest.getRequestUser().getName()).isEqualTo("Test Requestor User");
    }

    @Test
    void testFindAllByRequestUserIdNot() {
        //given
        Pageable pageable = PageRequest.of(0, 10);
        User requestorUser = createUser("requestor_user@gmail.com", "Test Requestor User");
        User anotherUser = createUser("another_user@gmail.com", "Another User");
        createRequest(requestorUser, "Test Request");

        //that
        List<Request> requests = requestRepository.findAllByRequestUserIdNot(anotherUser.getId(), pageable).getContent();

        //then
        assertThat(requests).hasSize(1);
        Request foundRequest = requests.get(0);
        assertThat(foundRequest.getDescription()).isEqualTo("Test Request");
        assertThat(foundRequest.getRequestUser().getName()).isEqualTo("Test Requestor User");
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Request createRequest(User requestorUser, String description) {
        Request request = new Request();
        request.setRequestUser(requestorUser);
        request.setDescription(description);
        request.setCreatedDate(LocalDateTime.now());
        return requestRepository.save(request);
    }

}
