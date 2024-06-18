package ru.practicum.shareit.request.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.shareit.request.model.Request;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * DTO for {@link Request}
 */
@Data
@Builder
@Jacksonized
public class RequestDto implements Serializable {

    Long id;

    Long requestorId;

    @NotEmpty
    String description;

}