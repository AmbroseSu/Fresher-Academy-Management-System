package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.UnitDTO;
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
import org.springframework.validation.annotation.Validated;
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

    @PreAuthorize("hasAuthority('unit:Full_Access')")
    @PostMapping("/user/search/hidden")
    public ResponseEntity<?> searchUserADMIN(@RequestBody UserDTO userDTO, @RequestParam(required = false) String sortByFirstName, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return userService.searchSortFilterADMIN(userDTO, sortByFirstName, page, limit);
    }

    @PreAuthorize("hasAuthority('unit:Full_Access')")
    @PostMapping("/user/search")
    public ResponseEntity<?> searchUser(@RequestBody UserDTO userDTO, @RequestParam(required = false) String sortByFirstName, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return userService.searchSortFilter(userDTO, sortByFirstName, page, limit);
    }

    @PreAuthorize("hasAuthority('user:Full_Access')")
    @GetMapping("/user/role")
    public ResponseEntity<?> getAllUserRoles() {
        return userRoleService.findAllUserRole();
    }

    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:View')")
    @GetMapping("/user")
    public ResponseEntity<?> getAllActiveUser(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return userService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('user:Full_Access')")
    @GetMapping("/user/hidden")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return userService.findAll(page, limit);
    }

//    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:View')")
//    @GetMapping("/user/{uuid}")
//    public ResponseEntity<?> getInfo(@PathVariable String uuid) {
//        return userService.findByUuid(uuid);
//    }

    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:View')")
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getInfo(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PreAuthorize("hasAuthority('user:Full_Access')")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return userService.changeStatus(id);
    }


    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:Modify')")
    @PutMapping("user/{id}")
    public ResponseEntity<?> updateUser(@Validated(AllFieldValidationGroup.class) @RequestBody UserDTO userDTO, @PathVariable(name = "id") Long id) {
        if (userService.checkExist(id)) {
            userDTO.setId(id);
            return userService.save(userDTO);
        }
        return ResponseUtil.error("Not found", "User does not exist", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:Modify')")
    @PutMapping("user/updateImage/{id}")
    public ResponseEntity<?> updateImage(@PathVariable(name = "id") Long id, @RequestParam(value = "image") MultipartFile image) {
        if (userService.checkExist(id)) {
            String avatarUrl = storageService.uploadFile(image);
            if (!avatarUrl.isBlank()) {
                return userService.updateUserImage(id, avatarUrl);
            }
            return ResponseUtil.error("Cannot update image", "Cannot save image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseUtil.error("Cannot update image", "User not found", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:Modify')")
    @PutMapping("user/permission/{userId}")
    public ResponseEntity<?> updateUserRolePermissions(@PathVariable Long userId, @RequestParam("syllabusPermission") Permission syllabusPermission, @RequestParam("materialPermission") Permission materialPermission, @RequestParam("trainingProgramPermission") Permission trainingProgramPermission, @RequestParam("learningObjectivePermission") Permission learningObjectivePermission, @RequestParam("unitPermission") Permission unitPermission, @RequestParam("classPermission") Permission classPermission, @RequestParam("contentPermission") Permission contentPermission, @RequestParam("userPermission") Permission userPermission) {
        userRoleService.updateUserRoleByUserId(userId, syllabusPermission, materialPermission, trainingProgramPermission, learningObjectivePermission, unitPermission, classPermission, contentPermission, userPermission);
        return new ResponseEntity<>("User role permissions updated successfully", HttpStatus.ACCEPTED);
    }


    @PreAuthorize("hasAuthority('user:Full_Access') || hasAuthority('user:Create')")
    @PostMapping("/user")
    public ResponseEntity<?> create(@Validated(AllFieldValidationGroup.class) @RequestBody UserDTO userDTO) {
        if (userDTO.getEmail() == null || userDTO.getEmail().isBlank()) {
            return ResponseUtil.error("Missing attribute", "email must not be null", HttpStatus.BAD_REQUEST);
        }
        return userService.save(userDTO);
    }
}
