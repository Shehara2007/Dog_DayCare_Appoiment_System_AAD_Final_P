package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.UserDTO;
import org.example.backend.service.custom.UserService;
import org.example.backend.util.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<APIResponse<String>> saveUser(@RequestBody UserDTO dto) {
        userService.saveUser(dto);
        return new ResponseEntity<>(new APIResponse<>(201, "User Saved Successfully", null), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<APIResponse<String>> updateUser(@RequestBody UserDTO dto) {
        userService.updateUser(dto);
        return new ResponseEntity<>(new APIResponse<>(200, "User Updated Successfully", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(new APIResponse<>(200, "Success", users), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<UserDTO>> getUserById(@PathVariable int id) {
        UserDTO user = userService.getUserById(id);
        return new ResponseEntity<>(new APIResponse<>(200, "Success", user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(new APIResponse<>(200, "User Deleted Successfully", null), HttpStatus.OK);
    }
}