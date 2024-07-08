package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.request.model.Request;

import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * DTO for {@link Request}
 */
@Data
@SuperBuilder
@Jacksonized
public class RequestDto implements Serializable {

    private Long id;

    private Long requestorId;

    @NotEmpty(message = "Не должно быть пустым")
    private String description;

    @JsonProperty("created")
    private String createdDate;

}