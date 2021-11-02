package projet.rest.data.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
@Data
@Entity
@Table (name="Subject")
public class Subject {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long SubjectId;	
	private String subjectName;
	private int hours;
	private int coefficient;
	private int level;
	@ManyToMany(mappedBy = "subjects", cascade = CascadeType.REMOVE)
    private List<Section> sections;
	@OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
	private List<Document> docs;
}

