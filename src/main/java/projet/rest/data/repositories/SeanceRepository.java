package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Seance;

public interface SeanceRepository extends JpaRepository<Seance, Long>{

}
