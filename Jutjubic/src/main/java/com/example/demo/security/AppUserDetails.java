package com.example.demo.security;

import com.example.demo.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    private final AppUser user;

    public AppUserDetails(AppUser user) { this.user = user; }
    public AppUser getUser() { return user; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); // bez rola za sada
    }

    @Override public String getPassword() { return user.getPasswordHash(); }
    @Override public String getUsername() { return user.getEmail(); } // login po email-u
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !user.isLocked(); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.isEnabled(); } // blokira login dok nije aktiviran
}
