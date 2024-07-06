package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * DTO for Request
 */
@Data
@SuperBuilder
@Jacksonized
public class RequestDto implements Serializable {

    Long id;

    Long requestorId;

    @NotEmpty(message = "Не должно быть пустым")
    String description;

    @JsonProperty("created")
    private String createdDate;

}