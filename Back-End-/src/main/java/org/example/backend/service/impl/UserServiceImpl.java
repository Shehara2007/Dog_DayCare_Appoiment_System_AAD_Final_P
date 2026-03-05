package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.example.backend.Repository.UserRepository;
import org.example.backend.dto.UserDTO;
import org.example.backend.service.custom.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already registered: " + userDTO.getEmail());
        }
        userRepository.save(modelMapper.map(userDTO, User.class));
    }

    @Override
    public void updateUser(UserDTO userDTO) {
        if (!userRepository.existsById(userDTO.getUserId())) {
            throw new RuntimeException("User not found with ID: " + userDTO.getUserId());
        }
        userRepository.save(modelMapper.map(userDTO, User.class));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return modelMapper.map(users, new TypeToken<List<UserDTO>>() {}.getType());
    }

    @Override
    public UserDTO getUserById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}