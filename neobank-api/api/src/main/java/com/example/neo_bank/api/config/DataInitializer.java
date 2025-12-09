package com.example.neo_bank.api.config;

import com.example.neo_bank.api.model.Role;
import com.example.neo_bank.api.model.User;
import com.example.neo_bank.api.repository.UserRepository;
import com.example.neo_bank.api.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, AccountService accountService, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail("admin@neobank.com")) {
                User admin = new User();
                admin.setName("Pablo Segura");
                admin.setEmail("admin@neobank.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                logger.info("DATOS INICIALES: ADMIN CREADO (admin@neobank.com)");
            }

            if (!userRepository.existsByEmail("andres@neobank.com")) {
                User user = new User();
                user.setName("Andres Gonzalez");
                user.setEmail("andres@neobank.com");
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(Role.USER);

                User savedUser = userRepository.save(user);
                accountService.createAccount(savedUser.getId());

                logger.info("DATOS INICIALES: USUARIO CREADO (andres@neobank.com)");
            }
        };
    }
}
