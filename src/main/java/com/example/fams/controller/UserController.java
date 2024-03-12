package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.UserDTO;
import com.example.fams.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserService userService;

//    ****ADMIN****
    @GetMapping("/admin/user")
    public ResponseEntity<?> getAllActiveUser(@RequestParam int page, @RequestParam int limit){
        return userService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("/admin/user/all")
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int limit){
        return userService.findAll(page, limit);
    }

    @DeleteMapping("/admin/user/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return userService.changeStatus(id);
    }
//    *************

//    ****USER****
    @GetMapping("/user/{uuid}")
    public ResponseEntity<?> getInfo(@PathVariable String uuid) {
        return userService.findByUuid(uuid);
    }

    @PutMapping("user/update/{id}")
    public ResponseEntity<?> updateLearningObjective(@Valid @RequestBody UserDTO userDTO, @PathVariable(name = "id") Long id) {
        if (userService.checkExist(id)) {
            userDTO.setId(id);
            return userService.save(userDTO);
        }
        return ResponseUtil.error("Not found","User does not exist", HttpStatus.NOT_FOUND);
    }

//    *************

    @PostMapping("user/create")
    public ResponseEntity<?> create(@RequestBody UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank() || userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return ResponseUtil.error("Missing attribute", "email and password must not be null", HttpStatus.BAD_REQUEST);
        }
        return userService.save(userDTO);
    }
}
