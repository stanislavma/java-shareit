package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * DTO for {@link Comment}
 */
@Builder
@Data
public class CommentDto implements Serializable {

    private Long id;

    private Long itemId;

    private String authorName;

    @JsonProperty("created")
    private String createdDate;

    @NotEmpty(message = "Комментарий является обязательным полем")
    private String text;

}