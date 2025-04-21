package fizy.web.app.controller;


import fizy.web.app.dto.UserDto;
import fizy.web.app.entity.User;
import fizy.web.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    //endpoints here
    @GetMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }

    /*public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        Var authObject = userService.authenticateUser(userDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, authObject.get("token").toString())
                .body(authObject.get("user"));
    }*/

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto userDto) {
        Map<String, Object> authObject = userService.authenticateUser(userDto);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, authObject.get("token").toString())
                .body(authObject.get("user"));
    }


}
