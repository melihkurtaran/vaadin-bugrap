package org.vaadin.bugrap.domain.spring;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User implements UserDetails {
    private static final long serialVersionUID = 1L;
    private org.vaadin.bugrap.domain.entities.Reporter userEnt;

    public User(org.vaadin.bugrap.domain.entities.Reporter fromEntity) {
        userEnt = fromEntity;
    }

    public org.vaadin.bugrap.domain.entities.Reporter getEntity() {
        return userEnt;
    }

    @Override
    public String toString() {
        return userEnt.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("USER"));
        if(userEnt.isAdmin()) roles.add(new SimpleGrantedAuthority("ADMIN"));
        return roles;
    }

    @Override
    public String getPassword() {
        return userEnt.getPassword();
    }

    @Override
    public String getUsername() {
        return userEnt.getName();
    }
}
