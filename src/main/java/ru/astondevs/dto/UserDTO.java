package ru.astondevs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Пользователь")
public class UserDTO {
    @Schema(description = "Уникальный идентификатор пользователя", example = "976890df-e67b-40fc-8e70-5e0d8c5e229a")
    private UUID id;

    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "Email", example = "ivan@example.com")
    private String email;

    @Schema(description = "Возраст", example = "25")
    private Integer age;
}
