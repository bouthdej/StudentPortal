package projet.rest.data.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="Club")
public class Club {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ClubId;	
	private String ClubName;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private String imageU;
   // @JoinColumn(name = "Owner_id", referencedColumnName = "id")
   // @OneToOne(cascade = CascadeType.ALL)
	private String clubOwner;
    @OneToMany(mappedBy = "clubPlanner", cascade = CascadeType.REMOVE)
	private List<Event> eventPlannedClub;
	@ManyToMany
    @JoinTable(name = "subscribed")
    @JsonIgnore
	private List<UserEntity> members;
	
}
