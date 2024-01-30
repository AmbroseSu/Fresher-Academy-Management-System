package com.example.fams;

import com.example.fams.entities.enums.Role;
import com.example.fams.entities.User;
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
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if(null == adminAccount){
            User user = new User();

            user.setEmail("admin@gmail.com");
            user.setFirstName("admin");
            user.setSecondName("admin");
            user.setRole(Role.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(user);
        }
    }
}
;