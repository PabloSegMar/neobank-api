package com.example.neo_bank.api.auth;

import com.example.neo_bank.api.model.Role;
import com.example.neo_bank.api.model.User;
import com.example.neo_bank.api.repository.UserRepository;
import com.example.neo_bank.api.security.JwtService;
import com.example.neo_bank.api.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsServiceImpl, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        logger.info("INTENTO DE LOGIN: Usuario {}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(request.getEmail());

        final String jwt = jwtService.generateToken(userDetails);

        logger.info("LOGIN EXITOSO para {}", request.getEmail());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User user) {
        logger.info("NUEVO REGISTRO: {}", user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        userRepository.save(user);

        final String jwt = jwtService.generateToken(user);
        logger.info("USUARIO CREADO con ID: {}", user.getId());
        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}
