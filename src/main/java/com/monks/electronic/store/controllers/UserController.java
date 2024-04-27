package com.monks.electronic.store.controllers;

import com.monks.electronic.store.dtos.ApiResponseMessage;
import com.monks.electronic.store.dtos.ImageResponse;
import com.monks.electronic.store.dtos.PageableResponse;
import com.monks.electronic.store.dtos.UserDTO;
import com.monks.electronic.store.services.FileService;
import com.monks.electronic.store.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FileService fileService;

    @Value("${user.profile.image.path}")
    private String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService,FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    //create
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO dto = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@Valid @RequestBody UserDTO userDTO, @PathVariable(name = "userId") String userId) {
        UserDTO dto = userService.updateUser(userDTO, userId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    //delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable(name = "userId") String userId) throws IOException {
        userService.deleteUser(userId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().message("User is deleted successfully !!").status(HttpStatus.OK).success(true).build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponseMessage);
    }

    //get all
    @GetMapping
    public ResponseEntity<PageableResponse<UserDTO>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
            ) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers(pageNumber, pageSize, sortBy, sortDir));
    }

    //get single
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable(name = "userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
    }

    //get by email
    @GetMapping("email/{userEmail}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable(name = "userEmail") String userEmail) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(userEmail));
    }

    //search user
    @GetMapping("search/{keyword}")
    public ResponseEntity<Collection<UserDTO>> searchUser(@PathVariable(name = "keyword") String keyword) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.searchUser(keyword));
    }

    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage")MultipartFile image, @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);

        /* update image name */
        UserDTO user = userService.getUserById(userId);
        user.setImageName(imageName);
        userService.updateUser(user,userId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .success(true)
                .status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);
    }

    //serve user image
    @GetMapping("image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDTO userDTO = userService.getUserById(userId);
        logger.info("User image name: {} ", userDTO.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, userDTO.getImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }
}
