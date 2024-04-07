package com.example.fams;

import com.example.fams.entities.User;
import com.example.fams.entities.UserRole;
import com.example.fams.entities.enums.Permission;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import com.example.fams.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class FamsApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    public static void main(String[] args) {
        SpringApplication.run(FamsApplication.class, args);
    }

    public void run(String... args) {
        if (userRoleRepository.count() == 0) {
            userRoleRepository.save(new UserRole(Role.SUPERADMIN, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access, Permission.Full_Access));
            userRoleRepository.save(new UserRole(Role.CLASSADMIN, Permission.Modify, Permission.Modify, Permission.Modify, Permission.Modify, Permission.Modify, Permission.Modify, Permission.Modify, Permission.Modify));
            userRoleRepository.save(new UserRole(Role.TRAINER, Permission.Create, Permission.Create, Permission.Create, Permission.Create, Permission.Create, Permission.Create, Permission.Create, Permission.Create));
        }

        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User FAMSuser = new User();
            FAMSuser.setEmail("admin@gmail.com");
            FAMSuser.setFirstName("admin");
            FAMSuser.setLastName("admin");
            FAMSuser.setUuid(UUID.randomUUID().toString());
            FAMSuser.setUserRole(userRoleRepository.findFirstByOrderByIdAsc());
            FAMSuser.setPassword(new BCryptPasswordEncoder().encode("admin"));
            FAMSuser.setPhone("0123456789");
            FAMSuser.setStatus(true);
            userRepository.save(FAMSuser);
        }

    }
}