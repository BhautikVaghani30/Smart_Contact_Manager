package com.s_c_m.smart_contect_manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.s_c_m.smart_contect_manager.Dao.UserRepository;
import com.s_c_m.smart_contect_manager.entities.User;

@Service
public class UserDetailsServicelmpl implements UserDetailsService{
@Autowired
private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // fetching user from database
        User user=userRepository.getUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("could not found user !!");
        }
        // CustomUserDetails customUserDetails = new CustomUserDetails(user);


        // throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
        return new CustomUserDetails(user);
    }
    
}
