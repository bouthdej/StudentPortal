package projet.rest.data.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="Section")
public class Section {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long SectionId;	
	private String sectionName;
	@OneToMany(mappedBy = "section", cascade = CascadeType.REMOVE)
	private List<Classe> classes;
	@ManyToMany
    @JoinTable(name = "SectionSubjects")
    @JsonIgnore
	private List<Subject> subjects;
}
