package com.s_c_m.smart_contect_manager.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.s_c_m.smart_contect_manager.entities.User;

public class CustomUserDetails implements UserDetails{

    private User user;

    public CustomUserDetails(User user) {
        super();
        this.user = user;
    }

    // https://chat.openai.com/c/0516f135-de61-4ba1-a793-76374d8ff336
    // user above reference to understanding this class

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

      SimpleGrantedAuthority simpleGrantedAuthority =  new SimpleGrantedAuthority(user.getRole());
        // throw new UnsupportedOperationException("Unimplemented method 'getAuthorities'");
        return List.of(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        // throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // throw new UnsupportedOperationException("Unimplemented method 'getUsername'");
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // throw new UnsupportedOperationException("Unimplemented method 'isAccountNonExpired'");
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { 
        // throw new UnsupportedOperationException("Unimplemented method 'isAccountNonLocked'");
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // throw new UnsupportedOperationException("Unimplemented method 'isCredentialsNonExpired'");
        return true;
    }

    @Override
    public boolean isEnabled() {      
        // throw new UnsupportedOperationException("Unimplemented method 'isEnabled'");
        return true;
    }
    
}
