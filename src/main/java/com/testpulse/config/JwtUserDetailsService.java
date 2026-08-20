package com.testpulse.config;

import com.testpulse.model.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtUserDetailsService {

    public CustomUserDetails loadUserByUsername(String username, String role) {
        return new CustomUserDetails(
                username,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
