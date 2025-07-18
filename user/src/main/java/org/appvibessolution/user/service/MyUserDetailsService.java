package org.appvibessolution.user.service;

import org.appvibessolution.user.model.AppUser;
import org.appvibessolution.user.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user =userRepo.findByEmail(email).orElseThrow(
                ()->new RuntimeException("User not found"));

        System.out.println("############"+email);

        if(user==null){
            System.out.println("User not found with userName: " + email);
            throw new UsernameNotFoundException("User not found with email : " + email);
        }

        return new UserPrincipal(user);
    }
}
