package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Subject;


public interface SubjectRepository extends JpaRepository<Subject, Long> {
	Subject findBySubjectName(String SubjectName);

}
