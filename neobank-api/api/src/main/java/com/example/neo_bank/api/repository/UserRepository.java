package com.example.neo_bank.api.repository;
import com.example.neo_bank.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    //Spring crea el SQL automáticamente al leer el nombre
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
