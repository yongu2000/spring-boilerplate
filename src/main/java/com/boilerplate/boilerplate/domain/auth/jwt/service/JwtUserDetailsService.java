package com.boilerplate.boilerplate.domain.auth.jwt.service;

import com.boilerplate.boilerplate.domain.auth.jwt.entity.JwtUserDetails;
import com.boilerplate.boilerplate.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new JwtUserDetails(userService.findByUsername(username));
    }
}
