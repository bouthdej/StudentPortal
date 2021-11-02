package projet.rest.data.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name="Event")
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long EventId;	
	private String EventName;
	private String EventDate;
	@ManyToOne( cascade = CascadeType.DETACH )
	private UserEntity planner;
	@ManyToOne( cascade = CascadeType.DETACH )
	private Club clubPlanner;

}
