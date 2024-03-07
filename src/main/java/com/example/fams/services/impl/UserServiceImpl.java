package com.example.fams.services.impl;

import com.example.fams.config.ConstraintViolationExceptionHandler;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.*;
import com.example.fams.entities.*;
import com.example.fams.repository.TrainingProgramRepository;
import com.example.fams.repository.UserClassRepository;
import com.example.fams.repository.UserRepository;
import com.example.fams.services.IGenericService;
import com.example.fams.services.ServiceUtils;
import com.example.fams.services.UserService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserClassRepository userClassRepository;
    private final GenericConverter genericConverter;


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
    public ResponseEntity<?> save(UpsertUserDTO upsertUserDTO) {
        try {
            // Cannot create a new user with this method
            // For create request, use the signup method in AuthenticationService
            if (upsertUserDTO.getEmail() == null) {
                return ResponseUtil.error("Update failed", "Email is required", HttpStatus.BAD_REQUEST);
            }

            // * For update request (if applicable)
            Optional<User> user1 = userRepository.findByEmail(upsertUserDTO.getEmail());
            if (user1.isEmpty()) {
                return ResponseUtil.error("Update failed", "User not found", HttpStatus.NOT_FOUND);
            }
            User user = user1.get();
            User tempOldUser = ServiceUtils.cloneFromEntity(user);
            // Apply updates efficiently using a converter, handling potential missing fields
            user = (User) genericConverter.updateEntity(upsertUserDTO, user);
//            user = ServiceUtils.fillMissingAttribute(user, userRepository.findByEmail(user.getEmail()).get());
            user = ServiceUtils.fillMissingAttribute(user, tempOldUser);

            // Save the user and handle potential errors
            user = userRepository.save(user);

            // Prepare the response with user information (filter sensitive data if needed)
            UpsertUserDTO result = (UpsertUserDTO) genericConverter.toDTO(user, UpsertUserDTO.class);

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

    private void convertListUserToListUserDTO(List<User> entities, List<UserDTO> result) {
        for (User user : entities){
            UserDTO newUserDTO = convertUserToUserDTO(user);
            result.add(newUserDTO);
        }
    }

    private UserDTO convertUserToUserDTO(User entity) {
        UserDTO newUserDTO = (UserDTO) genericConverter.toDTO(entity, UserDTO.class);
        List<FamsClass> classes = userClassRepository.findClassesByUserId(entity.getId());
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
        return newUserDTO;
    }

    public FamsClass convertDtoToEntity(ClassDTO classDTO, TrainingProgramRepository trainingProgramRepository) {
        FamsClass famsClass = new FamsClass();
        famsClass.setId(classDTO.getId());
        famsClass.setName(classDTO.getName());
        famsClass.setCode(classDTO.getCode());
        famsClass.setStatus(classDTO.getStatus());

        TrainingProgram trainingProgram = trainingProgramRepository.findOneById(classDTO.getTrainingProgramId());
        famsClass.setTrainingProgram(trainingProgram);

        return famsClass;
    }

}
