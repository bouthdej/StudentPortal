package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Section;


public interface SectionRepository extends JpaRepository<Section, Long> {
Section findBySectionName(String SectionName);
}
