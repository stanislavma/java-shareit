package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForOwnerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.common.Constants.X_SHARER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemServiceMock;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private ItemForOwnerDto itemForOwnerDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        itemForOwnerDto = ItemForOwnerDto.builder()
                .id(1L)
                .name("ItemName")
                .description("ItemDescription")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("CommentText")
                .build();
    }

    @Test
    void add_shouldReturnItem_whenItemIsValid() throws Exception {
        when(itemServiceMock.add(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void add_shouldReturnBadRequest_whenNameIsEmpty() throws Exception {
        ItemDto invalidItemDto = ItemDto.builder()
                .name("")
                .description("ItemDescription")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{name=Имя вещи является обязательным!}"));
    }

    @Test
    void add_shouldReturnBadRequest_whenDescriptionIsEmpty() throws Exception {
        ItemDto invalidItemDto = ItemDto.builder()
                .name("ItemName")
                .description("")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidItemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{description=Описание вещи не может быть пустым!}"));
    }

    @Test
    void update_shouldReturnItem_whenItemIsValid() throws Exception {
        when(itemServiceMock.update(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getById_shouldReturnItem_whenItemExists() throws Exception {
        when(itemServiceMock.getById(anyLong(), anyLong()))
                .thenReturn(itemForOwnerDto);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$.name").value(itemForOwnerDto.getName()))
                .andExpect(jsonPath("$.description").value(itemForOwnerDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemForOwnerDto.getAvailable()));
    }

    @Test
    void getById_shouldReturnError_whenItemNotExist() throws Exception {
        long nonExistentItemId = 999L;
        String expectedErrorMessage = "Вещь не найдена: " + nonExistentItemId;

        when(itemServiceMock.getById(anyLong(), anyLong())).thenThrow(new EntityNotFoundException(expectedErrorMessage));

        mockMvc.perform(get("/items/{itemId}", nonExistentItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(expectedErrorMessage));
    }

    @Test
    void getItemsByOwnerId_shouldReturnItems_whenItemsExist() throws Exception {
        when(itemServiceMock.getItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemForOwnerDto));

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemForOwnerDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemForOwnerDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemForOwnerDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemForOwnerDto.getAvailable()));
    }

    @Test
    void getItemsByText_shouldReturnItems_whenItemsMatchText() throws Exception {
        when(itemServiceMock.getItemsByText(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "Item")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void addComment_shouldReturnComment_whenCommentIsValid() throws Exception {
        when(itemServiceMock.addComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }

    @Test
    void addComment_shouldReturnBadRequest_whenCommentTextIsEmpty() throws Exception {
        CommentDto invalidCommentDto = CommentDto.builder()
                .text("")
                .build();

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(X_SHARER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("{text=Комментарий является обязательным полем}"));
    }

}