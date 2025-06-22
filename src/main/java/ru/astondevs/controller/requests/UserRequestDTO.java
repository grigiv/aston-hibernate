package ru.astondevs.controller.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO запроса на создание пользователя")
public class UserRequestDTO {
    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;

    @Schema(description = "Email", example = "ivan@example.com")
    @Email
    private String email;

    @Schema(description = "Возраст", example = "25")
    @Min(0)
    @Max(150)
    private Integer age;
}
