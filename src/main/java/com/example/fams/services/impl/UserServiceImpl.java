package com.example.fams.services.impl;

import com.example.fams.config.ConstraintViolationExceptionHandler;
import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.*;
import com.example.fams.entities.*;
import com.example.fams.repository.*;
import com.example.fams.services.EmailService;
import com.example.fams.services.ServiceUtils;
import com.example.fams.services.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ClassUserRepository classUserRepository;
    private final GenericConverter genericConverter;
    private final ClassRepository classRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    public ResponseEntity<?> findByUuidOrId(String identifier) {
        User user;
        if (StringUtils.isNumeric(identifier)) {
            // Search by ID
            Long id = Long.parseLong(identifier);
            user = userRepository.findByStatusIsTrueAndId(id);
        } else {
            // Search by UUID
            user = userRepository.findByStatusIsTrueAndUuid(identifier);
        }

        if (user == null) {
            return ResponseUtil.error("Not found","User not found", HttpStatus.NOT_FOUND);
        }

        UserDTO result = convertUserToUserDTO(user);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }


    @Override
    public ResponseEntity<?> findByUuid(String uuid) {
        User user = userRepository.findByStatusIsTrueAndUuid(uuid);
        UserDTO result = convertUserToUserDTO(user);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        User user = userRepository.findByStatusIsTrueAndId(id);
        UserDTO result = convertUserToUserDTO(user);
        return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        List<User> entities = userRepository.findAllByStatusIsTrue(pageable);
        List<UserDTO> result = new ArrayList<>();

        convertListUserToListUserDTO(entities, result);

        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                result.size());
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {

        Pageable pageable = PageRequest.of(page - 1, limit);
        List<User> entities = userRepository.findAllByOrderByIdDesc(pageable);
        List<UserDTO> result = new ArrayList<>();

        convertListUserToListUserDTO(entities, result);

        return ResponseUtil.getCollection(result,
                HttpStatus.OK,
                "Fetched successfully",
                page,
                limit,
                result.size());

    }

    @Override
    public ResponseEntity<?> save(UserDTO userDTO) {
        try {
            ServiceUtils.errors.clear();
            List<Long> requestClassIds = userDTO.getClassIds();
            User user;

            if (requestClassIds != null) {
                ServiceUtils.validateClassIds(requestClassIds, classRepository);
            }
            if (!ServiceUtils.errors.isEmpty()) {
                throw new CustomValidationException(ServiceUtils.errors);
            }

            // * For update request (if applicable)
            if (userDTO.getId() != null) {
                User oldEntity = userRepository.findById(userDTO.getId());
                User tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
                user = convertDtoToEntity(userDTO);
                ServiceUtils.fillMissingAttribute(user, tempOldEntity);
                loadClassUserFromListClassId(requestClassIds, user.getId());
                user.markModified();
                userRepository.save(user);
            } else {
                // * For create new user
                Optional<User> tempUser = userRepository.findByEmail(userDTO.getEmail());

                if (tempUser.isPresent()) {
                    return ResponseUtil.error("Create failed", "Email already exists!", HttpStatus.NOT_FOUND);
                }

                user = convertDtoToEntity(userDTO);
                // * Set UUID lần đầu tiên tạo
                user.setUuid(UUID.randomUUID().toString());
                user.markModified();
                userRepository.save(user);
                loadClassUserFromListClassId(requestClassIds, user.getId());

                // * Gửi email cho người được thêm vào
                emailService.sendWelcomeEmail(userDTO.getEmail(), userDTO.getFirstName(), userDTO.getPassword());
            }

            UserDTO result = convertUserToUserDTO(user);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Update successful");
        } catch (ConstraintViolationException e) {
            return ConstraintViolationExceptionHandler.handleConstraintViolation(e);
        } catch (Exception e) { // Handle other potential exceptions
            return ResponseUtil.error(e.getMessage(), "Update failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            return ResponseUtil.error("Change status failed", "User not found", HttpStatus.NOT_FOUND);
        }
        User newUser = user;
        newUser.setStatus(!newUser.getStatus());
        newUser.isEnabled();
        userRepository.save(newUser);
        return ResponseUtil.getObject(genericConverter.toDTO(newUser,UserDTO.class), HttpStatus.OK, "Change status successful");
    }

    @Override
    public Boolean checkExist(Long id) {
        User user = userRepository.findById(id);
        return user != null;
    }

    private void convertListUserToListUserDTO(List<User> entities, List<UserDTO> result) {
        for (User user : entities){
            UserDTO newUserDTO = convertUserToUserDTO(user);
            result.add(newUserDTO);
        }
    }

    private UserDTO convertUserToUserDTO(User entity) {
        UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(entity, UserDTO.class);
        List<FamsClass> classes = classUserRepository.findClassByUserId(entity.getId());
        if (entity.getClassUsers() == null){
            newUserDTO.setClassIds(null);
        }
        else {
            // ! Set list learningObjectiveIds và unitId sau khi convert ở trên vào contentDTO
            List<Long> classIds = classes.stream()
                    .map(FamsClass::getId)
                    .toList();

            newUserDTO.setClassIds(classIds);
        }
        newUserDTO.setPassword(null);
        return newUserDTO;
    }

    public User convertDtoToEntity(UserDTO userDTO) {
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setPhone(userDTO.getPhone());
        user.setDob(userDTO.getDob());
        user.setGender(userDTO.getGender());
        user.setStatus(userDTO.getStatus());

        return user;
    }

    private void loadClassUserFromListClassId(List<Long> requestClassIds, Long userId) {
        if (requestClassIds != null && !requestClassIds.isEmpty()) {
            User user = userRepository.findById(userId);
            if (user != null) {
                for (Long requestClassId: requestClassIds) {
                    FamsClass famsClass = classRepository.findById(requestClassId);
                    if (famsClass != null) {
                        ClassUser classUser = new ClassUser();
                        classUser.setFamsClass(famsClass);
                        classUser.setUser(user);
                        classUserRepository.save(classUser);
                    }
                }
            }

        }
    }

}
