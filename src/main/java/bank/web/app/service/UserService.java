package bank.web.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bank.web.app.dto.UserDto;
import bank.web.app.entity.User;
import bank.web.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User registerUser(UserDto userDto) {
        User user = mapToUser(userDto);
        return userRepository.save(user);
    }

    public Map<String, Object> authenticateUser(UserDto userDto) {
        Map<String, Object> authObject = new HashMap<String, Object>();

        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
        );
        authObject.put("token", "Bearer ".concat(jwtService.generateToken(userDto.getUsername())));
        authObject.put("user", user);
        return authObject;
    }

    private User mapToUser(UserDto userDto) {
        return User.builder()
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .dob(userDto.getDob())
                .tel(userDto.getTel())
                .gender(userDto.getGender())
                .tag("io-" + userDto.getUsername())
                .roles(List.of("USER"))
                .build();
    }
}
