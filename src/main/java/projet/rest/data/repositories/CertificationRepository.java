package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Certification;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
