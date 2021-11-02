package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Classe;

public interface ClasseRepository extends JpaRepository<Classe, Long>{
	Classe findByClassName(String className);
}
