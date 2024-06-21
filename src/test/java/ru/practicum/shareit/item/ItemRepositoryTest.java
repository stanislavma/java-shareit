package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ItemRepositoryIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user_owner@gmail.com");
        user.setName("Owner User");
        user = userRepository.save(user);

        Item item1 = new Item();
        item1.setOwner(user);
        item1.setName("Test Item 1");
        item1.setDescription("Description for test item 1");
        item1.setAvailable(true);
        itemRepository.save(item1);

        Item item2 = new Item();
        item2.setOwner(user);
        item2.setName("Test Item 2");
        item2.setDescription("Description for test item 2");
        item2.setAvailable(true);
        itemRepository.save(item2);
    }

    @Test
    void testFindAllByOwnerId() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findAllByOwnerId(user.getId(), pageable);
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
        Pageable pageable = PageRequest.of(0, 10);
        List<Item> items = itemRepository.findByText("test item", pageable);
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
        Long requestId = 1L; // assuming the request ID exists
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        List<Item> items = itemRepository.findAllByRequestId(requestId, sort);

        assertThat(items).isEmpty(); // as we have not set requestId for items, it should be empty
    }

}