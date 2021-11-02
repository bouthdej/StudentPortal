package projet.rest.data.models;

import java.util.Date;
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

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
	
@Data
@Entity
@Table (name="Mail")
public class MailEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long mailId;	
	private String Object;
	private String Body;
	@ManyToMany
    @JoinTable(name = "MailRecieved")
    @JsonIgnore
	private List<UserEntity> recievers;
   // @JsonIgnore
	@ManyToOne
	private UserEntity Sender;
	//ne9ess el time w date
    @CreationTimestamp
    private Date dateofcreation ;
    private int spam=0;

}	
	