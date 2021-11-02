package projet.rest.data.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="Seance")
public class Seance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long SeanceId;
	@ManyToOne( cascade = CascadeType.DETACH )
	private Classe classe;
	private int numClass;
	private String subjectName;
	@JsonIgnore
	@ManyToOne( cascade = CascadeType.DETACH )
	private UserEntity prof;
	private int numSeance;
	private String dayOfWeek;
	private String period; //perFortnight-weekly-..
	
}
