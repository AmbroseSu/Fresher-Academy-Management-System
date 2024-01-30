package com.example.fams;

import com.example.fams.entities.FAMS_user;
import com.example.fams.entities.enums.Role;
import com.example.fams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FamsApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(FamsApplication.class, args);
    }

    public void run(String... args) {
        FAMS_user adminAccount = userRepository.findByRole(Role.ADMIN);
        if(null == adminAccount){
            FAMS_user FAMSuser = new FAMS_user();

            FAMSuser.setEmail("admin@gmail.com");
            FAMSuser.setFirstName("admin");
            FAMSuser.setSecondName("admin");
            FAMSuser.setRole(Role.ADMIN);
            FAMSuser.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(FAMSuser);
        }
    }
}
;