package projet.rest.data.models;


import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="Class")
public class Classe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ClasseId;	
	private String className;
	private int classLevel;
	@JsonIgnore
	@ManyToOne( cascade = CascadeType.DETACH )
	private Section section;
	@ManyToMany
    @JoinTable(name = "ClasseUsers")
    @JsonIgnore
	private List<UserEntity> users;
	@OneToMany(mappedBy = "classe", cascade = CascadeType.REMOVE)
	private List<Seance> seances;
	@OneToMany(mappedBy = "classe", cascade = CascadeType.REMOVE)
	private List<Document> docs;
}
