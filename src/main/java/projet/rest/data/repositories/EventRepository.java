package projet.rest.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import projet.rest.data.models.Event;

public interface EventRepository extends JpaRepository<Event, Long> {

}
