package projet.rest.data.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
    private UserRepository userRepo;
    @Override
public UserDetails loadUserByUsername(String email)  throws UsernameNotFoundException {
    	UserEntity user =userRepo.findByEmail(email);
        if(user==null)
            throw new UsernameNotFoundException("User Not Found ! ! !");
        return new UserPrincipal(user);
    }

} 
