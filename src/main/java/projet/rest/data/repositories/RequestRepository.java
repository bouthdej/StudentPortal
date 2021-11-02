package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Request;

public interface RequestRepository extends JpaRepository<Request, Long>{

}
