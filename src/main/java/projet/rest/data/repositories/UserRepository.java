package projet.rest.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.MailEntity;
import projet.rest.data.models.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    //UserEntity findByUsername(String username);
	//List<MailEntity> findBymailsSent(UserEntity user);

}
