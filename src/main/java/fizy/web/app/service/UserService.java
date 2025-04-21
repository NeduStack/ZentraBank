package fizy.web.app.service;

import fizy.web.app.entity.User;
import fizy.web.app.dto.UserDto;
import fizy.web.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository; //Inject the UserRepository
    private final PasswordEncoder passwordEncoder; //Inject the PasswordEncoder
    private final UserDetailsService userDetailsService; //Inject the UserDetailsService


    private final AuthenticationManager authenticationManager; //Inject the AuthenticationManager
    private final JwtService jwtService; //Inject the JwtService

    public User registerUser(UserDto userDto) {
        User user = mapToUser(userDto);

        // Set default role if applicable
        // user.setRoles(Set.of("USER"));

        return userRepository.save(user);
    }

    public Map<String, Object> authenticateUser(UserDto userDto) {
        Map<String, Object> authObject = new HashMap<String, Object>();
        User user = (User) userDetailsService.loadUserByUsername(userDto.getUsername());
        if (user == null){
            throw new UsernameNotFoundException("User not found");
        }

        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
        authObject.put("token", "Bearer ".concat(jwtService.generateToken(userDto.getUsername())));
        authObject.put("user", user);
        return authObject;
    }

    private User mapToUser(UserDto dto){
        return User.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .dob(dto.getDob())
                .tel(dto.getTel())
                .tag("zen_" + dto.getUsername())
                .roles(List.of("USER"))
                .build();
    }


}