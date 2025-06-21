package ru.astondevs.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.controller.requests.UserRequestDTO;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.service.UserService;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = "CRUD API работы с пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))})
    })
    public CollectionModel<EntityModel<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        List<EntityModel<UserDTO>> userModels = users.stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(methodOn(UserController.class).updateUser(user.getId(), null)).withRel("update"),
                        linkTo(methodOn(UserController.class).deleteUser(user.getId())).withRel("delete")))

                .toList();

        return CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить данные пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным id не найден", content = @Content)
    })
    public UserDTO getUserById(
            @Parameter(description = "Id пользователя", example = "976890df-e67b-40fc-8e70-5e0d8c5e229a")
            @PathVariable("id") UUID id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Указаны некорректные данные запроса", content = @Content)
    })
    public void createUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        userService.saveUser(userRequestDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Изменить данные пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные пользователя изменены"),
            @ApiResponse(responseCode = "400", description = "Указаны некорректные данные запроса", content = @Content)
    })
    public UserDTO updateUser(
            @Parameter(description = "Id пользователя", example = "976890df-e67b-40fc-8e70-5e0d8c5e229a")
            @PathVariable("id") UUID id,
            @RequestBody @Valid UserRequestDTO userRequestDTO) {
        return userService.updateUser(id, userRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь с указанным id не найден")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "Id пользователя", example = "976890df-e67b-40fc-8e70-5e0d8c5e229a")
            @PathVariable("id") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
