package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
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