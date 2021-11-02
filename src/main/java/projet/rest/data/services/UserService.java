package projet.rest.data.services;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import projet.rest.data.models.Announce;
import projet.rest.data.models.Certification;
import projet.rest.data.models.Classe;
import projet.rest.data.models.Club;
import projet.rest.data.models.Document;
import projet.rest.data.models.MailEntity;
import projet.rest.data.models.Payement;
import projet.rest.data.models.Request;
import projet.rest.data.models.Seance;
import projet.rest.data.models.Section;
import projet.rest.data.models.Subject;
import projet.rest.data.models.UserEntity;

public interface UserService {
	
	////////////////**User**////////////
	List<UserEntity> getAllUserEntity();
	List<UserEntity> getUserByRole(String Role);
    UserEntity getUserEntityById(long id);
    UserEntity createUserEntity(UserEntity entity );
    UserEntity deleteUserEntity(long id);
    UserEntity modifyUserEntity(long id, UserEntity newUser);
    
    ////////////////**Token**////////////
    void DelTokenByIdUser(long i);
    
    ////////////////**Mail**///////////
    public void SendMailUser (MailEntity mail);
    public void SendMailToClass (MailEntity mail);
    public List<MailEntity> getAllMails ();
    public List<MailEntity> getAllRecievedMails (String mail);
    public List<MailEntity> getAllSentMails (String mail);
    public List<MailEntity> getAllSpamMails (String mail);
     
    ////////////////**Section**////////////
    List<Section> getAllSection();
 	Section getSectionById(long id);
 	Section createSection(Section section ,String SectionName);
 	Section deleteSection(long id);
 	Section modifySection(long id, Section newSection);
 	
    ////////////////**club**////////////
    List<Club> getAllClubs();
    Club getClubById(long id);
    Club createClub(Club club);
    Club deleteClub(long id);
    Club modifyClub(long id, Club newclub);
    
    ////////////////**Subject**////////////
    List<Subject> getAllSubjects();
    Subject getSubjectById(long id);
    Subject createSubject(Subject subject);
    Subject deleteSubject(long id);
    Subject modifySubject(long id, Subject newSubject);
    
	////////////////**Classes**////////////
	List<Classe> getAllClasses();
	Classe getClasseById(long id);
	Classe createClasse(Classe classe);
	Classe deleteClasse(long id);
	Classe modifyClasse(long id, Classe newClasse);

	////////////////**Seances**////////////
	List<Seance> getAllSeances();
	Seance getSeanceById(long id);
	Seance createSeance(Seance seance);
	Seance deleteSeance(long id);
	Seance modifySeance(long id, Seance newSeance);
	
	////////////////**Payement**////////////
	List<Payement > getAllPayements();
	Payement  getPayementById(long id);
	List<Payement>  getAllByUser(String email);
	Payement  createPayement(Payement Payement);
	Payement  deletePayement(long id);
	Payement  modifyPayement(long id, Payement newPayement);
	public float getAllByPaid(String email);
	public float getAllNonPaid(String email);
	
	////////////////**Document**////////////
	public Document  createDoc(MultipartFile file,Classe classe,UserEntity prof, Subject subject);
	public Optional<Document> getDocById(long id);
	public List<Document> getDocs();
	
	////////////////**Request**////////////
	List<Request> getAllRequests();
	Request getRequestById(long id);
	Request createRequest(Request Request);
	Request deleteRequest(long id);
	Request modifyRequest(long id, Request newRequest);

	////////////////**Certification**////////////
	List<Certification> getAllCertification();
	Certification getCertificationById(long id);
	Certification createCertification(Certification Certification);
	Certification deleteCertification(long id);
	Certification modifyCertification(long id, Certification newCertification);
		
	////////////////**Announce**////////////
	List<Announce> getAllAnnounce();
	Announce getAnnounceById(long id);
	Announce createAnnounce(Announce Announce);
	Announce deleteAnnounce(long id);
	Announce modifyAnnounce(long id, Announce newAnnounce);

}
