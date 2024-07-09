package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for Comment
 */
@Builder
@Data
public class CommentDto implements Serializable {

    private Long id;

    private Long itemId;

    private String authorName;

    @JsonProperty("created")
    private String createdDate;

    @NotNull
    @NotBlank(message = "Комментарий является обязательным полем")
    @Size(max = 300, message = "Максимальная длина - 300 символов")
    private String text;

}