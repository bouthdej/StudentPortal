package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Club;
import projet.rest.data.models.UserEntity;

public interface ClubRepository extends JpaRepository<Club, Long> {
}
