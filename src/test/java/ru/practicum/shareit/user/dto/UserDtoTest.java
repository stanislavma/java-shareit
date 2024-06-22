package ru.practicum.shareit.user.dto;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivanov@gmail.com")
                .build();

        String jsonValue = "{\"id\":1,\"name\":\"Ivan\",\"email\":\"ivanov@gmail.com\"}";

        assertThat(json.write(userDto)).isEqualToJson(jsonValue);
    }

    @Test
    void testDeserialize() throws Exception {
        String jsonValue = "{\"id\":1,\"name\":\"Ivan\",\"email\":\"ivanov@gmail.com\"}";

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivanov@gmail.com")
                .build();

        assertThat(json.parse(jsonValue)).usingRecursiveComparison().isEqualTo(userDto);
    }

}
