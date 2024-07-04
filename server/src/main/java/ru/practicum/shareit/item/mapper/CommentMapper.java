package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        String createdDate = DATE_TIME_FORMATTER.format(LocalDateTime.now());

        return CommentDto.builder()
                .id(comment.getId())
                .itemId(comment.getItem() != null ? comment.getItem().getId() : null)
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .createdDate(createdDate)
                .text(comment.getText())
                .build();
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Comment toEntity(CommentDto dto, Item item, User author) {
        if (dto == null) {
            return null;
        }

        return Comment.builder()
                .item(item)
                .author(author)
                .createdDate(LocalDateTime.now())
                .text(dto.getText())
                .build();
    }

}
