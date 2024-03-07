package com.example.fams.controller;

import com.example.fams.dto.LearningObjectiveDTO;
import com.example.fams.dto.UpsertUserDTO;
import com.example.fams.dto.UserDTO;
import com.example.fams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    UserService userService;

//    ****ADMIN****
    @GetMapping("/admin/user/")
    public ResponseEntity<?> getAllActiveUser(@RequestParam int page, @RequestParam int limit){
        return userService.findAllByStatusTrue(page, limit);
    }
    @GetMapping("/admin/user/all")
    public ResponseEntity<?> getAllUser(@RequestParam int page, @RequestParam int limit){
        return userService.findAll(page, limit);
    }

    @DeleteMapping("/admin/user/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userService.changeStatus(id);
    }
//    *************

//    ****USER****
    @GetMapping("/user/{uuid}")
    public ResponseEntity<?> getInfo(@PathVariable String uuid) {
        return userService.findByUuid(uuid);
    }

    @PutMapping("user/update")
    public ResponseEntity<?> updateLearningObjective(@RequestBody UserDTO userDTO) {
        return userService.save(userDTO);
    }

//    *************
}
