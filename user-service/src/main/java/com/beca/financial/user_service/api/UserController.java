package com.beca.financial.user_service.api;


import com.beca.financial.user_service.api.dto.CreateUserRequest;
import com.beca.financial.user_service.api.dto.ImportUserResponse;
import com.beca.financial.user_service.api.dto.UpdateUserRequest;
import com.beca.financial.user_service.api.dto.UserResponse;
import com.beca.financial.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@RequestBody @Valid CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @GetMapping("/all")
    public Page<UserResponse> findAll(@RequestParam(required = false) Boolean onlyActive, Pageable pageable){
        return userService.findAll(onlyActive, pageable);
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        return  userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.inactive(id);
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.OK)
    public ImportUserResponse importUser(@RequestParam("file") MultipartFile file) {
        return userService.importUser(file);
    }
}
