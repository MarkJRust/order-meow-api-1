package com.ordermeow.api.user;

import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;

@RestController
@RequestMapping()
@Api(tags = {"User API"})
public class UserController {

    private final UserDetailsServiceImpl userDetailsService;


    @Autowired
    public UserController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @ApiOperation(value = "Register", notes = "Registers with the given credentials.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Authentication.class),
            @ApiResponse(code = 400, message = "Bad Request", response = Object.class)
    })
    @PostMapping("/register")
    public void register(
            @ApiParam(name = "UserEntity", value = "user", required = true, format = MediaType.APPLICATION_JSON_VALUE, example = "{username: \"user\", \"password\": \"test123\"}")
            @RequestBody UserEntity user) {

        userDetailsService.createUser(user);
    }

    /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Login", notes = "Login with the given credentials.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success", response = Authentication.class)})
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    void login(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    /**
     * Implemented by Spring Security
     */
    @ApiOperation(value = "Logout", notes = "Logout the current user.")
    @ApiResponses({@ApiResponse(code = 200, message = "Success")})
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    void logout() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }
}
