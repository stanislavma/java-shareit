package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("user@gmail.com");
        user.setName("Test User");
        user = userRepository.save(user);

        item = new Item();
        item.setOwner(user);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item = itemRepository.save(item);

        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setText("Полезная вещь!");
        comment.setCreatedDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Test
    void testFindAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        assertThat(comments).hasSize(1);

        Comment foundComment = comments.get(0);
        assertThat(foundComment.getText()).isEqualTo("Полезная вещь!");
        assertThat(foundComment.getItem().getName()).isEqualTo("Test Item");
        assertThat(foundComment.getAuthor().getName()).isEqualTo("Test User");
    }

}
