package UserProduct.userproductapplication.Controller;

import UserProduct.userproductapplication.JWT.JwtUtil;
import UserProduct.userproductapplication.Model.User;
import UserProduct.userproductapplication.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register User
    //http://localhost:8082/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if the user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("User already exists"));
        }

        // Ensure that the role is not null
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Default role
        }

        // Encrypt the password and save the user
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse("User registered successfully"));
    }

    // Login User
    //http://localhost:8082/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Validate user credentials
        Optional<User> existingUser = userRepository.findByUsername(loginRequest.getUsername());
        if (existingUser.isPresent()) {
            User foundUser = existingUser.get(); // Unwrap the Optional to get the User object
            if (passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
                String token = jwtUtil.generateToken(foundUser.getUsername());
                return ResponseEntity.ok(new LoginResponse("User successfully logged in", token));
            }
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Invalid username or password"));
    }

    // Inner class for LoginRequest
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Inner class for ApiResponse
    public static class ApiResponse {
        private String message;

        public ApiResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    // Inner class for LoginResponse
    public static class LoginResponse {
        private String message;
        private String token;

        public LoginResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
