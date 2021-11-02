package projet.rest.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.MailEntity;
import projet.rest.data.models.UserEntity;

public interface MailRepository extends JpaRepository<MailEntity, Long> {
	List<MailEntity> findByRecievers(UserEntity user);
	MailEntity findByMailId(long MailId);
}
