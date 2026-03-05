package org.example.backend.service.custom;

import org.example.backend.dto.UserDTO;

import java.util.List;

public interface UserService {
    void saveUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(int id);
    void deleteUser(int id);
}