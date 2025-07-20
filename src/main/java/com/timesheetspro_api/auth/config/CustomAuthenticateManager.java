package com.timesheetspro_api.auth.config;

import com.timesheetspro_api.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomAuthenticateManager implements AuthenticationManager {
    @Autowired
    UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails user = userService.loadUserByUsername(username);

        if (user != null) {
            String decryptPassword = user.getPassword();
            if (username.equalsIgnoreCase(user.getUsername()) && decryptPassword.equals(password)) {
//                return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
                return new UsernamePasswordAuthenticationToken(username, password);
            } else {
                throw new BadCredentialsException("Invalid Credentials");
            }
        } else {
            throw new UsernameNotFoundException("User Not Found");
        }
    }
}
