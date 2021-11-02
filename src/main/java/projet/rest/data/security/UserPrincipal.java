package projet.rest.data.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import projet.rest.data.models.UserEntity;

 

public class UserPrincipal implements UserDetails {

 

    private UserEntity user;
    
    
    public UserPrincipal(UserEntity user) {
        super();
        this.user = user;
    }

 

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
    	return Collections.singleton(new SimpleGrantedAuthority(this.user.getRole()));
        
    }
    
    public Collection<? extends GrantedAuthority> getEmail() {
        
    	return Collections.singleton(new SimpleGrantedAuthority(this.user.getEmail()));
        
    }
 

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return user.getPassword();
    }

 

    @Override
    public String getUsername() {
        // TODO Auto-generated method stub
        return user.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

 

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

 

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

 

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

 

}
