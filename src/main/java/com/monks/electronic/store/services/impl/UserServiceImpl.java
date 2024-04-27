package com.monks.electronic.store.services.impl;

import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.dtos.UserDTO;
import com.monks.electronic.store.entities.User;
import com.monks.electronic.store.exceptions.ResourceNotFound;
import com.monks.electronic.store.helper.ObjectListToPageableResponse;
import com.monks.electronic.store.repositories.UserRepository;
import com.monks.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    @Value("${user.profile.image.path}")
    private String imagePath;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO createUser(UserDTO userDto) {
        String uuid = UUID.randomUUID().toString();
        userDto.setId(uuid);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = dtoToEntity(userDto);
        User savedUser = userRepository.save(user);
        return entityToDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDto, String userId) {
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFound("User not found with given id !!"));
        user.setName(userDto.getName());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());
        user.setAbout(userDto.getAbout());

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(String id) throws IOException {
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found for given id. Hence can't delete !!"));
        /* delete user profile image */
        String fullPath = imagePath + user.getImageName();
        Files.delete(Path.of(fullPath));

        /* now delete user */
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        /* pageNumber default starts from 0 */
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> pageUsers = userRepository.findAll(pageable);
        List<User> userList = pageUsers.getContent();
        List<UserDTO> dtoList = userList.stream().map(this::entityToDto).toList();

        return ObjectListToPageableResponse.getPageableResponse(pageUsers, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("User not found with given id !!"));
        return entityToDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("User not found for given email !!!"));
        return entityToDto(user);
    }

    @Override
    public Collection<UserDTO> searchUser(String name) {
        List<User> userList = userRepository.findByNameContaining(name);
        return userList.stream().map(this::entityToDto).toList();
    }


    private User dtoToEntity(UserDTO userDto) {
//        return User.builder()
//                .id(userDto.getId())
//                .about(userDto.getAbout())
//                .gender(userDto.getGender())
//                .imageName(userDto.getImageName())
//                .password(userDto.getPassword())
//                .name(userDto.getName())
//                .email(userDto.getEmail()).build();
        return modelMapper.map(userDto, User.class);
    }

    public UserDTO entityToDto(User user) {
//        return UserDTO.builder()
//                .id(user.getId())
//                .about(user.getAbout())
//                .gender(user.getGender())
//                .imageName(user.getImageName())
//                .password(user.getPassword())
//                .name(user.getName())
//                .email(user.getEmail()).build();
        return modelMapper.map(user, UserDTO.class);
    }
}
