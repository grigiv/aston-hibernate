package ru.astondevs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.astondevs.controller.requests.UserRequestDTO;
import ru.astondevs.dto.UserDTO;
import ru.astondevs.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable("id") UUID id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody UserRequestDTO userRequestDTO) {
        userService.saveUser(userRequestDTO);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") UUID id, @RequestBody UserRequestDTO userRequestDTO) {
        userService.updateUser(id, userRequestDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUser(id);
    }
}
