package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.UserDTO;
import com.example.fams.entities.User;
import com.example.fams.entities.enums.Permission;
import com.example.fams.repository.UserRoleRepository;
import com.example.fams.services.UserService;
import com.example.fams.services.impl.StorageServiceImpl;
import com.example.fams.services.impl.UserRoleServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    @Autowired
    UserRoleServiceImpl userRoleService;
    @Autowired
    UserService userService;
    @Autowired
    private StorageServiceImpl storageService;

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
    public ResponseEntity<?> updateUser(@Valid @ModelAttribute UserDTO userDTO,
                                        @PathVariable(name = "id") Long id) {
        if (userService.checkExist(id)) {
            userDTO.setId(id);
            if (userDTO.getAvatar() != null) {
                String avatarUrl = storageService.uploadFile(userDTO.getAvatar());
                if (!avatarUrl.isBlank()) {
                    userDTO.setAvatarUrl(avatarUrl);
                }
            }
            return userService.save(userDTO);
        }
        return ResponseUtil.error("Not found","User does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("user/update/permission/{userId}")
    public ResponseEntity<?> updateUserRolePermissions(@PathVariable Long userId,
                                                       @RequestParam("syllabusPermission") Permission syllabusPermission,
                                                       @RequestParam("materialPermission") Permission materialPermission,
                                                       @RequestParam("trainingProgramPermission") Permission trainingProgramPermission,
                                                       @RequestParam("learningObjectivePermission") Permission learningObjectivePermission,
                                                       @RequestParam("unitPermission") Permission unitPermission,
                                                       @RequestParam("classPermission") Permission classPermission,
                                                       @RequestParam("contentPermission") Permission contentPermission,
                                                       @RequestParam("userPermission") Permission userPermission) {
        userRoleService.updateUserRoleByUserId(userId, syllabusPermission, materialPermission, trainingProgramPermission, learningObjectivePermission, unitPermission, classPermission, contentPermission, userPermission);
        return new ResponseEntity<>("User role permissions updated successfully", HttpStatus.ACCEPTED);
    }



//    *************

//    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:Create')")
    @PostMapping("/auth/signup")
    public ResponseEntity<?> create(@Valid @RequestBody UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank() || userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            return ResponseUtil.error("Missing attribute", "email and password must not be null", HttpStatus.BAD_REQUEST);
        }
        return userService.save(userDTO);
    }
}
