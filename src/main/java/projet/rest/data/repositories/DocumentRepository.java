package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Document;

public interface DocumentRepository extends JpaRepository<Document, Long>{
}
