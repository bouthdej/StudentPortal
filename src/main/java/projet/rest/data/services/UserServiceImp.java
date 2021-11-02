package projet.rest.data.services;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import projet.rest.data.models.Announce;
import projet.rest.data.models.Certification;
import projet.rest.data.models.Classe;
import projet.rest.data.models.Club;
import projet.rest.data.models.ConfirmationToken;
import projet.rest.data.models.Document;
import projet.rest.data.models.MailEntity;
import projet.rest.data.models.Payement;
import projet.rest.data.models.Request;
import projet.rest.data.models.Seance;
import projet.rest.data.models.Section;
import projet.rest.data.models.Subject;
import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.AnnounceRepository;
import projet.rest.data.repositories.CertificationRepository;
import projet.rest.data.repositories.ClasseRepository;
import projet.rest.data.repositories.ClubRepository;
import projet.rest.data.repositories.ConfirmationTokenRepository;
import projet.rest.data.repositories.DocumentRepository;
import projet.rest.data.repositories.MailRepository;
import projet.rest.data.repositories.PayementRepository;
import projet.rest.data.repositories.RequestRepository;
import projet.rest.data.repositories.SeanceRepository;
import projet.rest.data.repositories.SectionRepository;
import projet.rest.data.repositories.SubjectRepository;
import projet.rest.data.repositories.UserRepository;

@Service
public class UserServiceImp implements UserService{

	@Autowired
   	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	SendEmailService SendEmailService;
	private UserRepository repoUser;
	private ConfirmationTokenRepository repostoken;
	private MailRepository repoMail;
	private SectionRepository repoSection;
	private ClubRepository repoClub;
	private SubjectRepository repoSubject;
	private ClasseRepository repoClasse;
	private SeanceRepository repoSeance;
	private PayementRepository repoPayement;
	private DocumentRepository repoDoc;
	private CertificationRepository repoCert;
	private RequestRepository reporeq;
	private AnnounceRepository repoAn;
	
	@Autowired
    public UserServiceImp(UserRepository repoUser,ConfirmationTokenRepository repostoken,MailRepository repoMail, SectionRepository repoSection,
    		ClubRepository repoClub, SubjectRepository repoSubject , ClasseRepository repoClasse,
    		 SeanceRepository repoSeance, PayementRepository repoPayement,DocumentRepository repoDoc,
    		 CertificationRepository repoCert,RequestRepository reporeq,AnnounceRepository repoAn) {
        super();
        //this.repostoken=repostoken;
        this.repoUser = repoUser;
        this.repostoken = repostoken;
        this.repoMail = repoMail;
        this.repoSection=repoSection;
        this.repoClub=repoClub;
        this.repoSubject = repoSubject;
        this.repoClasse = repoClasse;
        this.repoSeance = repoSeance;
        this.repoPayement = repoPayement;
        this.repoDoc = repoDoc;  
        this.repoCert = repoCert;
        this.reporeq = reporeq;
        this.repoAn = repoAn;
        }
	 @Override
	    public List<UserEntity> getAllUserEntity() {
	        return repoUser.findAll();
	    }
	 @Override
	    public UserEntity getUserEntityById(long id) {
	        Optional<UserEntity> opt = repoUser.findById(id);
	        UserEntity entity;
	        if (opt.isPresent())
	            entity = opt.get();
	        else
	            throw new NoSuchElementException("User with id : "+id+" is not found");
	        return entity; 
	    }
	 public static String generateRandomPassword()
	    { int len=10;
	        // ASCII range – alphanumeric (0-9, a-z, A-Z)
	        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789^$*:!,.-_";
	 
	        SecureRandom random = new SecureRandom();
	        StringBuilder sb = new StringBuilder();
	 
	        // each iteration of the loop randomly chooses a character from the given
	        // ASCII range and appends it to the `StringBuilder` instance
	 
	        for (int i = 0; i < len; i++)
	        {
	            int randomIndex = random.nextInt(chars.length());
	            sb.append(chars.charAt(randomIndex));
	        }
	 
	        return sb.toString();
	    }
	 @Override
     public UserEntity createUserEntity(UserEntity entity) {
     	//String password = bCryptPasswordEncoder.encode(entity.getPassword());
		 String password = generateRandomPassword();
		 String text="To connect, you need to create a new password. Your current password is : "
                 +password;
         SendEmailService.verifyEmail(entity.getEmail(),"Account Created!",text);
		 System.out.println("$$$$$password="+password);
		 entity.setPassword(bCryptPasswordEncoder.encode(password));
		 
		 UserEntity User = repoUser.save(entity);
                                 
                 return User;
     }
	 @Override
	    public UserEntity deleteUserEntity(long id) {
	        UserEntity entity = this.getUserEntityById(id);
	        repoUser.deleteById(id);
	        return entity;
	    }
	 @Override
	 public void DelTokenByIdUser(long i ) {
			List<ConfirmationToken> tokens = repostoken.findAll();
			for(ConfirmationToken t : tokens )
			{
				if(t.getUser().getId()==i)
					repostoken.deleteById(t.getTokenid());
			}}
	@Override
	public List<UserEntity> getUserByRole(String Role) {
		List<UserEntity>	 user = repoUser.findAll();
		List<UserEntity>  userbyrole = new ArrayList<UserEntity>();
		for(UserEntity u : user )
		{
			if(u.getRole().equalsIgnoreCase(Role))
				userbyrole.add(u);
			
		}
	return userbyrole;
	}
	@Override
	public UserEntity modifyUserEntity(long id, UserEntity newUser) {
		UserEntity oldUser = this.getUserEntityById(id);
        System.out.println("password 2 = '"+newUser.getPassword()+"'");	
        if (!newUser.getFirstName().equals(""))    
            oldUser.setFirstName(newUser.getFirstName());
        if (!newUser.getLastName().equals(""))    
            oldUser.setLastName(newUser.getLastName());
        if (!newUser.getPassword().equals(""))
        {   
        	oldUser.setPassword(newUser.getPassword());
        	String password = bCryptPasswordEncoder.encode(newUser.getPassword());
        	oldUser.setPassword(password);
        }
        if (!newUser.getPhone().equals(""))    
            oldUser.setPhone(newUser.getPhone());
        if (newUser.getBirthDate() != null)
            oldUser.setBirthDate(newUser.getBirthDate());
        if (newUser.getRole() != null)
            oldUser.setRole(newUser.getRole());
        if (newUser.getImageU() != null)
            oldUser.setImageU(newUser.getImageU());
        
       return repoUser.save(oldUser);
		
	}
	//////////////////////Mail Entity///////////////////////
	@Override
    public void SendMailUser (MailEntity mail) {
		repoMail.save(mail);
    	SendEmailService.sendEmail(mail.getSender().getEmail(),mail.getBody(),mail.getObject(),mail.getRecievers());
    }
	//sendEmailToMany
	@Override
    public void SendMailToClass (MailEntity mail) {
		System.out.println("test 2");
		repoMail.save(mail);
		System.out.println("test 3");
    	SendEmailService.sendEmailToMany(mail.getSender().getEmail(),mail.getBody(),mail.getObject(),mail.getRecievers());
    }
	@Override
	public List<MailEntity> getAllMails () {
        return repoMail.findAll();
	}
	@Override
	public List<MailEntity> getAllRecievedMails (String mail) {
        List <MailEntity> recieved= new ArrayList<MailEntity>();
        for (MailEntity mailEntity : repoMail.findByRecievers(repoUser.findByEmail(mail))) {
			if(mailEntity.getSpam()==0)
				recieved.add(mailEntity);
		}
        return recieved;
	} 
	
	@Override
	public List<MailEntity> getAllSpamMails (String mail) {
        List <MailEntity> recieved= new ArrayList<MailEntity>();
        for (MailEntity mailEntity : repoMail.findByRecievers(repoUser.findByEmail(mail))) {
			if(mailEntity.getSpam()==1)
				recieved.add(mailEntity);
		}
        return recieved;
	}
	@Override
	public List<MailEntity> getAllSentMails (String mail) {
        List <MailEntity> sent= new ArrayList<MailEntity>();
        for (MailEntity mailEntity : repoMail.findAll()) {
				if(mailEntity.getSender().getEmail().equalsIgnoreCase(mail))
					sent.add(mailEntity);}
        return sent;
	}
	
	////////////////**Section**////////////
	@Override
	public List<Section> getAllSection() {
		
		return repoSection.findAll();
	}
	@Override
	public Section getSectionById(long id) {
		Optional<Section> opt = repoSection.findById(id);
		Section sect;
        if (opt.isPresent())
            sect = opt.get();
        else
            throw new NoSuchElementException("Section with id : "+id+" is not found");
        return sect;
	}
	
	@Override
	public Section createSection(Section section ,String SectionName) {
		section.setSectionName(SectionName); 
		Section Sect = repoSection.save(section);
        return Sect;
	}
	@Override
	public Section deleteSection(long id) {
		Section sect = this.getSectionById(id);
        repoSection.deleteById(id);
        return sect;
	}
	@Override
	public Section modifySection(long id, Section newSection) {
		Section oldSection = this.getSectionById(id);
     
        if (!newSection.getSectionName().equals(""))    
        	oldSection.setSectionName(newSection.getSectionName());
             
       return repoSection.save(oldSection);
	}
	////////////////**Club**////////////

	@Override
	public List<Club> getAllClubs() {
		return repoClub.findAll();
	}
	@Override
	public Club getClubById(long id) {
		Optional<Club> opt = repoClub.findById(id);
		Club club;
        if (opt.isPresent())
        	club = opt.get();
        else
            throw new NoSuchElementException("Club with id : "+id+" is not found");
        return club;
	}
	@Override
	public Club createClub(Club club) {
		//club.getClubOwner().setOwn(club);
		//System.out.println(club.getClubOwner().getFirstName());
		UserEntity owner = repoUser.findByEmail(club.getClubOwner());
		owner.setOwnClub(club.getClubName());
        return repoClub.save(club);		
	}
	@Override
	public Club deleteClub(long id) {
		Club club = this.getClubById(id);
        repoClub.deleteById(id);
        return club;
	}
	@Override
	public Club modifyClub(long id, Club newclub) {
		Club oldClub = this.getClubById(id);
	     
        if (!newclub.getClubName().equals(""))    
        	oldClub.setClubName(newclub.getClubName());
             
       return repoClub.save(oldClub);
	} 
	////////////////**Subject**////////////
	@Override
	public List<Subject> getAllSubjects() {
		return repoSubject.findAll();
	}
	@Override
	public Subject getSubjectById(long id) {
		Optional<Subject> opt = repoSubject.findById(id);
		Subject subj;
        if (opt.isPresent())
        	subj = opt.get();
        else
            throw new NoSuchElementException("Subject with id : "+id+" is not found");
        return subj;
	}
	
	@Override
	public Subject createSubject(Subject subject) {
		return repoSubject.save(subject);
	}
	@Override
	public Subject deleteSubject(long id) {
		Subject subj = this.getSubjectById(id);
        repoSubject.deleteById(id);
        return subj;
	}
	@Override
	public Subject modifySubject(long id, Subject newSubject) {
		Subject oldSubject = this.getSubjectById(id);
     
        if (!newSubject.getSubjectName().equals(""))    
        	oldSubject.setSubjectName(newSubject.getSubjectName());
             
       return repoSubject.save(oldSubject);
	}	
	////////////////**Classe**////////////
	@Override
	public List<Classe> getAllClasses() {
	return repoClasse.findAll();
	}
	@Override
	public Classe getClasseById(long id) {
	Optional<Classe> opt = repoClasse.findById(id);
	Classe cl;
	if (opt.isPresent())
	cl = opt.get();
	else
	throw new NoSuchElementException("Classe with id : "+id+" is not found");
	return cl;
	}
	@Override
	public Classe createClasse(Classe classe) {
	return repoClasse.save(classe);
	}
	@Override
	public Classe deleteClasse(long id) {
	Classe cl = this.getClasseById(id);
	repoClasse.deleteById(id);
	return cl;
	}
	@Override
	public Classe modifyClasse(long id, Classe newClasse) {
	Classe oldClasse = this.getClasseById(id);
	
	if (!newClasse.getClassName().equals(""))    
	oldClasse.setClassName(newClasse.getClassName());
	 
	return repoClasse.save(oldClasse);
	}
	@Override
	public List<Seance> getAllSeances() {
		return repoSeance.findAll();
	}
	@Override
	public Seance getSeanceById(long id) {
		Optional<Seance> opt = repoSeance.findById(id);
		Seance seance;
		if (opt.isPresent())
			seance = opt.get();
		else
		throw new NoSuchElementException("Seance with id : "+id+" is not found");
		return seance;		
	}
	@Override
	public Seance createSeance(Seance seance) {
		return repoSeance.save(seance);
	}
	@Override
	public Seance deleteSeance(long id) {
		Seance seance = this.getSeanceById(id);
		repoSeance.deleteById(id);
		return seance;
	}
	@Override
	public Seance modifySeance(long id, Seance newSeance) {
		Seance oldSeance = this.getSeanceById(id);
		
		if (!newSeance.getSubjectName().equals(""))    
		oldSeance.setSubjectName(newSeance.getSubjectName());
		return repoSeance.save(oldSeance);
	}
	
	////////////Payement//////////////
	@Override
	public List<Payement> getAllPayements() {
		return repoPayement.findAll();

	}
	@Override
	public Payement  getPayementById(long id) {
		Optional<Payement> opt = repoPayement.findById(id);
		Payement payement;
		if (opt.isPresent())
			payement = opt.get();
		else
		throw new NoSuchElementException("Payement with id : "+id+" is not found");
		return payement;	
	}
	@Override
	public Payement  createPayement(Payement payement) {
		return repoPayement.save(payement);
	}
	@Override
	public Payement  deletePayement(long id) {
		Payement Payement = this.getPayementById(id);
		repoPayement.deleteById(id);
		return Payement;
	}
	@Override
	public Payement modifyPayement(long id, Payement newPayement) {
		Payement oldPayement  = this.getPayementById(id);
		
		if (!newPayement.getMotifPayement().equals(""))    
		oldPayement .setMotifPayement(newPayement.getMotifPayement());
		return repoPayement.save(oldPayement);
	}
	@Override
	public List<Payement> getAllByUser(String email){
		List<Payement> pays = new ArrayList<>();
		for (Payement payement : this.getAllPayements()) {
			if(payement.getEtudiant().getEmail().equalsIgnoreCase(email))
				pays.add(payement);
		}
		return pays;
	}
	@Override
	public float getAllByPaid(String email){
		float result = 0;
		for (Payement payement : this.getAllByUser(email)) {
			result+=payement.getMontant();
		}
		return result;
	}
	@Override
	public float getAllNonPaid(String email) {
		String pm = repoUser.findByEmail(email).getPayementMode();
		if(pm.equalsIgnoreCase("m1")) {
			return 5500-this.getAllByPaid(email);
		}
		else if (pm.equalsIgnoreCase("m2")) {
			return 4000-this.getAllByPaid(email);
		}
		else {
			return 2000-this.getAllByPaid(email);
		}
	}
	////////////Document//////////////
	@Override
	public Document  createDoc(MultipartFile file,Classe classe,UserEntity prof, Subject subject) {
		String docName = file.getOriginalFilename();
		try {
			Document doc = new Document(docName, file.getContentType(), file.getBytes(), classe, subject, prof);
			return repoDoc.save(doc);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public Optional<Document> getDocById(long id){
		return repoDoc.findById(id);
	}
	@Override
	public List<Document> getDocs(){
		return repoDoc.findAll();
	}
	////////////Request//////////////
	@Override
	public List<Request> getAllRequests() {
		return reporeq.findAll();
	}
	@Override
	public Request getRequestById(long id) {
		Optional<Request> opt = reporeq.findById(id);
		Request Request;
        if (opt.isPresent())
        	Request = opt.get();
        else
            throw new NoSuchElementException("Request with id : "+id+" is not found");
        return Request; 
	}
	@Override
	public Request createRequest(Request Request) {
		return reporeq.save(Request);
	}
	@Override
	public Request deleteRequest(long id) {
		Request Request = this.getRequestById(id);
        reporeq.deleteById(id);
        return Request;
	}
	@Override
	public Request modifyRequest(long id, Request newRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	////////////Certification//////////////
	@Override
	public List<Certification> getAllCertification() {
		return repoCert.findAll();
	}
	@Override
	public Certification getCertificationById(long id) {
		Optional<Certification> opt = repoCert.findById(id);
		Certification Certification;
        if (opt.isPresent())
        	Certification = opt.get();
        else
            throw new NoSuchElementException("Certification with id : "+id+" is not found");
        return Certification; 
	}
	@Override
	public Certification createCertification(Certification Certification) {
		return repoCert.save(Certification);
	}
	@Override
	public Certification deleteCertification(long id) {
		Certification Certification = this.getCertificationById(id);
        repoCert.deleteById(id);
        return Certification;
	}
	@Override
	public Certification modifyCertification(long id, Certification newCertification) {
		// TODO Auto-generated method stub
		return null;
	}
	///////////////////////Announce/////////////
	@Override
	public List<Announce> getAllAnnounce() {
		return repoAn.findAll();
	}
	@Override
	public Announce getAnnounceById(long id) {
		Optional<Announce> opt = repoAn.findById(id);
		Announce Announce;
        if (opt.isPresent())
        	Announce = opt.get();
        else
            throw new NoSuchElementException("Announce with id : "+id+" is not found");
        return Announce; 
	}
	@Override
	public Announce createAnnounce(Announce Announce) {
		return repoAn.save(Announce);

	}
	@Override
	public Announce deleteAnnounce(long id) {
		Announce Announce = this.getAnnounceById(id);
        repoAn.deleteById(id);
        return Announce;
	}
	@Override
	public Announce modifyAnnounce(long id, Announce newAnnounce) {
		// TODO Auto-generated method stub
		return null;
	}
}
