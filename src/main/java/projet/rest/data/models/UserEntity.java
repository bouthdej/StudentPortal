package projet.rest.data.models;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="UserEntity")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	//@Column(name = "First Name",length = 50,nullable = false)
	private String firstName;
	private String lastName;
	private String password; //prof qal mayet7atech el password fel table houni
	//@DateTimeFormat(pattern="dd-mm-yyyy HH:mm:ss")
	private String birthDate;
	private String email;
	@OneToMany(mappedBy = "Sender", cascade = CascadeType.REMOVE)
	private List<MailEntity> mailsSent;
	private String phone;
	private String Role;
	//private String className;
	private String gender;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private String imageU;
    private int verified=0;
    @ManyToMany(mappedBy = "users", cascade = CascadeType.REMOVE)
    private List<Classe> classes;
    @ManyToMany(mappedBy = "recievers", cascade = CascadeType.REMOVE)
    private List<MailEntity> revieved;
   // @OneToOne( cascade = CascadeType.DETACH )
    private String OwnClub;
    @ManyToMany(mappedBy = "members", cascade = CascadeType.REMOVE)
    private List<Club> subscribed;
    @OneToMany(mappedBy = "planner", cascade = CascadeType.REMOVE)
	private List<Event> eventPlanned;
    @OneToMany(mappedBy = "prof", cascade = CascadeType.REMOVE)
	private List<Seance> seances;
    
    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.REMOVE)
	private List<Payement> transactions;
    private String payementMode;
    @OneToMany(mappedBy = "prof", cascade = CascadeType.REMOVE)
	private List<Document> docs;
	
}
