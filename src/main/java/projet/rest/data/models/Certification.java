package projet.rest.data.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table (name="Certification")
public class Certification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long CertificationId;	
	private String certificationName;
	
}
