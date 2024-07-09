package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    private Long id;

    private Long requestorId;

    @NotNull
    @NotBlank(message = "Не должно быть пустым")
    @Size(max = 300, message = "Максимальная длина - 300 символов")
    private String description;

    @JsonProperty("created")
    private String createdDate;

}