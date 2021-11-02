package projet.rest.data.models;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table (name="Payement")
public class Payement {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long PayementId;	
	private float montant;
	private String MotifPayement;
	private String ModePayement;
	private String Banque;
	private String numeroCheque;
	private String numRecu;
	@Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

	@JsonIgnore
	@ManyToOne( cascade = CascadeType.DETACH )
	private UserEntity etudiant;
	

	 
	    public Payement() {
	    	createdDate = new Date();
	        numRecu = UUID.randomUUID().toString();
	    }

		public Payement(long payementId, float montant, String motifPayement, String modePayement, String banque,
				String numeroCheque,  UserEntity etudiant) {
			this.montant = montant;
			MotifPayement = motifPayement;
			ModePayement = modePayement;
			Banque = banque;
			this.numeroCheque = numeroCheque;
			this.etudiant = etudiant;
			createdDate = new Date();
	        numRecu = UUID.randomUUID().toString();
		}
}
