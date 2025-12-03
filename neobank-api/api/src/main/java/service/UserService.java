package service;

import model.User;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
public class UserService {

    // Necesitamos el repositorio para hablar con la base de datos
    private final UserRepository userRepository;

    // Constructor para inyección de dependencias
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        } else {
            return userRepository.save(user);
        }
    }
}
