package dev.sorokin.paymentsystem.api;

import dev.sorokin.paymentsystem.api.dto.CreateUserRequest;
import dev.sorokin.paymentsystem.api.dto.UserDto;
import dev.sorokin.paymentsystem.domain.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        return userService.getUserOrThrow(id);
    }
}
