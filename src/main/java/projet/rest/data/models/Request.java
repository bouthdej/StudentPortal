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
@Table (name="Request")
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long RequestId;	
	private String studentEmail;
	private String subject;
	private String message;
	private int checked=0;
}
