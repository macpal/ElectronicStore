package com.monks.electronic.store.services;

import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.dtos.UserDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;

@Service
public interface UserService {
    //create
    UserDTO createUser(UserDTO userDto);

    //update
    UserDTO updateUser(UserDTO userDto, String userId);

    //delete
    void deleteUser(String id) throws IOException;

    //get all users
    PageableResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir );

    //get single user by id
    UserDTO getUserById(String id);

    //get single user by email
    UserDTO getUserByEmail(String email);

    //search user
    Collection<UserDTO> searchUser(String name);

    //other user specific features
}
