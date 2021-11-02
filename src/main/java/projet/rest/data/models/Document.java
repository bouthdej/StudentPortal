package projet.rest.data.models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name="Document")
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long DocId;	
	private String DocName;
	private String DocType;
	@Lob
	private byte[] data;
	
	@ManyToOne( cascade = CascadeType.DETACH )
	private Classe classe;
	@ManyToOne( cascade = CascadeType.DETACH )
	private Subject subject;
	@ManyToOne( cascade = CascadeType.DETACH )
	private UserEntity prof;
	public Document(String docName, String docType, byte[] data, Classe classe, Subject subject,
			UserEntity prof) {
		super();
		DocName = docName;
		DocType = docType;
		this.data = data;
		this.classe = classe;
		this.subject = subject;
		this.prof = prof;
	}
	public Document() {
		
	}
	
}
