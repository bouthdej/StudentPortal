package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Announce;

public interface AnnounceRepository extends JpaRepository<Announce, Long>{

}
