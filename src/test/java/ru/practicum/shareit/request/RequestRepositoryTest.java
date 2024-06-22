package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
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

    private User requestorUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        requestorUser = new User();
        requestorUser.setEmail("requestor_user@gmail.com");
        requestorUser.setName("Test Requestor User");
        requestorUser = userRepository.save(requestorUser);

        anotherUser = new User();
        anotherUser.setEmail("another_user@gmail.com");
        anotherUser.setName("Another User");
        anotherUser = userRepository.save(anotherUser);

        Request request = new Request();
        request.setRequestUser(requestorUser);
        request.setDescription("Test Request");
        request.setCreatedDate(LocalDateTime.now());
        requestRepository.save(request);
    }

    @Test
    void testFindAllByRequestUserId() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdDate");
        List<Request> requests = requestRepository.findAllByRequestUserId(requestorUser.getId(), sort);
        assertThat(requests).hasSize(1);

        Request foundRequest = requests.get(0);
        assertThat(foundRequest.getDescription()).isEqualTo("Test Request");
        assertThat(foundRequest.getRequestUser().getName()).isEqualTo("Test Requestor User");
    }

    @Test
    void testFindAllByRequestUserIdNot() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Request> requests = requestRepository.findAllByRequestUserIdNot(anotherUser.getId(), pageable).getContent();
        assertThat(requests).hasSize(1);

        Request foundRequest = requests.get(0);
        assertThat(foundRequest.getDescription()).isEqualTo("Test Request");
        assertThat(foundRequest.getRequestUser().getName()).isEqualTo("Test Requestor User");
    }

}
