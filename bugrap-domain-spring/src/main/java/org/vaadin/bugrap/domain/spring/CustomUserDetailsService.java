package org.vaadin.bugrap.domain.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.vaadin.bugrap.domain.entities.Reporter;
import org.vaadin.bugrap.domain.spring.repositories.ReporterRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private ReporterRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Reporter user = userRepository.getByNameOrEmail(username, username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(user);
    }

}
