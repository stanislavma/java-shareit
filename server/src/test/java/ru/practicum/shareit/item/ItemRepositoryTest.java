package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void testFindAllByOwnerId() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User user = createUser("user_owner@gmail.com", "Owner User");
        createItem(user, "Test Item 1", "Description for test item 1", null);
        createItem(user, "Test Item 2", "Description for test item 2", null);

        //that
        List<Item> items = itemRepository.findAllByOwnerId(user.getId(), pageable);

        //then
        assertThat(items).hasSize(2);

        Item foundItem1 = items.stream().filter(item -> item.getName().equals("Test Item 1"))
                .findFirst().orElse(null);

        Item foundItem2 = items.stream().filter(item -> item.getName().equals("Test Item 2"))
                .findFirst().orElse(null);

        assertThat(foundItem1).isNotNull();
        assertThat(foundItem1.getDescription()).isEqualTo("Description for test item 1");
        assertThat(foundItem1.getAvailable()).isTrue();

        assertThat(foundItem2).isNotNull();
        assertThat(foundItem2.getDescription()).isEqualTo("Description for test item 2");
        assertThat(foundItem2.getAvailable()).isTrue();
    }

    @Test
    void testFindByText() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        User user = createUser("user_owner@gmail.com", "Owner User");
        createItem(user, "Test Item 1", "Description for test item 1", null);
        createItem(user, "Test Item 2", "Description for test item 2", null);

        //that
        List<Item> items = itemRepository.findByText("test item", pageable);

        //then
        assertThat(items).hasSize(2);

        Item foundItem1 = items.stream().filter(item -> item.getName().equals("Test Item 1")).findFirst().orElse(null);
        Item foundItem2 = items.stream().filter(item -> item.getName().equals("Test Item 2")).findFirst().orElse(null);

        assertThat(foundItem1).isNotNull();
        assertThat(foundItem1.getDescription()).isEqualTo("Description for test item 1");
        assertThat(foundItem1.getAvailable()).isTrue();

        assertThat(foundItem2).isNotNull();
        assertThat(foundItem2.getDescription()).isEqualTo("Description for test item 2");
        assertThat(foundItem2.getAvailable()).isTrue();
    }

    @Test
    void testFindAllByRequestId() {
        //given
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        User user = createUser("user_owner@gmail.com", "Owner User");
        Request request = createRequest(user);
        createItem(user, "Test Item 1", "Description for test item 1", request);

        //that
        List<Item> items = itemRepository.findAllByRequestId(request.getId(), sort);

        //then
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getName()).isEqualTo("Test Item 1");
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return userRepository.save(user);
    }

    private Request createRequest(User user) {
        Request request = new Request();
        request.setRequestUser(user);
        request.setDescription("Описание необходимого товара");
        request.setCreatedDate(LocalDateTime.now());
        request = requestRepository.save(request);
        return request;
    }

    private Item createItem(User user, String name, String description, Request request) {
        Item item = new Item();
        item.setOwner(user);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        item.setRequest(request);
        return itemRepository.save(item);
    }

}