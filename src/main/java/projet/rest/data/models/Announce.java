package projet.rest.data.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table (name="Announce")
public class Announce {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long AnnounceId;
	private String title;
	private String body;
	@Lob
	@Column(columnDefinition = "MEDIUMBLOB")
	private String imageU;
	@Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
	public Announce() {
    	createdDate = new Date();
    }
}
