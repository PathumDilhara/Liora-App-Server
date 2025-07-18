package org.appvibessolution.user.service;

import org.appvibessolution.user.model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final AppUser user;

    public UserPrincipal(AppUser user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("User")); // TODO: can add more roles
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // always allow login
    }

    @Override
    public boolean isAccountNonLocked() {

        if (!user.isAccountLocked()){
            return true; // account not locked
        }

        Duration lockDuration = Duration.between(user.getLockTime(), LocalDateTime.now());

        if(lockDuration.toMinutes() >= 10){
                // since now has passed the ExpiryTime Unlock account
                user.setAccountLocked(false);
                user.setLockTime(null);
                return true;
        }

        return false; // account locked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Subscription info

    public boolean hasActiveSubscription() {
        LocalDateTime expiry = user.getSubscriptionExpiryDate();
        return expiry != null && LocalDateTime.now().isBefore(expiry);
    }
}
