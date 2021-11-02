package projet.rest.data.endpoints;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import projet.rest.data.models.Announce;
import projet.rest.data.models.Certification;
import projet.rest.data.models.Classe;
import projet.rest.data.models.Club;
import projet.rest.data.models.ConfirmationToken;
import projet.rest.data.models.Document;
import projet.rest.data.models.MailEntity;
import projet.rest.data.models.Payement;
import projet.rest.data.models.Seance;
import projet.rest.data.models.Subject;
import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.ClasseRepository;
import projet.rest.data.repositories.ClubRepository;
import projet.rest.data.repositories.ConfirmationTokenRepository;
import projet.rest.data.repositories.DocumentRepository;
import projet.rest.data.repositories.MailRepository;
import projet.rest.data.repositories.SubjectRepository;
import projet.rest.data.repositories.UserRepository;
import projet.rest.data.services.SendEmailService;
import projet.rest.data.services.UserService;
import projet.rest.data.models.Request;

@Controller
@Data
@AllArgsConstructor
@RequestMapping("/student")
public class StudentController {
	@Autowired
	UserService service ;
	private UserRepository userRepo;
	private MailRepository mailRepo;
	private ClubRepository clubRepo;
	private ConfirmationTokenRepository conftrepo;
	private DocumentRepository docrepo;
	private ClasseRepository classRepo;
	private SubjectRepository subjectRepo;
	
	@Autowired
	SendEmailService SendEmailService;
	//***********************************
	public String CheckRole () {
		Collection<? extends GrantedAuthority> authorities;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    authorities = auth.getAuthorities();
	     
	    return authorities.toArray()[0].toString();
	}
	public String getUserUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username;
		if (principal instanceof UserDetails) {
		 username = ((UserDetails)principal).getUsername();
		} else {
		 username = principal.toString();
		 
		}
		return username;
	}
	
	//***********************************
	@GetMapping("/home")
	public String studentindex(Model model) {
		System.out.println("-------------"+getUserUsername());
		if (userRepo.findByEmail(getUserUsername()).getVerified()==0) {
				return "redirect:/confirm-account/"+getUserUsername();			
		}
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List <Club> clubs = service.getAllClubs();
		model.addAttribute("clubs",clubs);
		List <Announce> Allans = service.getAllAnnounce();
		List <Announce> ans	= Allans.subList(Math.max(Allans.size() - 3, 0), Allans.size());
		model.addAttribute("ans",ans);
		Classe classe =  user.getClasses().get(0);
		String day = LocalDate.now().getDayOfWeek().name();
		List<Seance> seances=new ArrayList<Seance>();
		for (Seance s : classe.getSeances()) {
			if(s.getDayOfWeek().equalsIgnoreCase(day)) {
				seances.add(s);
			}
		}
		model.addAttribute("day",day);
		model.addAttribute("seances",seances);
	    return "student/studentindex";
	}
	////////////////Mail//////////////////
	@GetMapping("/inbox")
	public String inbox(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/inbox";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		
		List<MailEntity> recieved =  service.getAllRecievedMails(getUserUsername());
		Collections.reverse(recieved);
		model.addAttribute("recieved",recieved);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/mail/inbox";
	}
	@GetMapping("/sent")
	public String sent(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/sent/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		List<MailEntity> sent =  service.getAllSentMails(getUserUsername());
		Collections.reverse(sent);
		model.addAttribute("sent",sent);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/mail/sent";
	}
	@GetMapping("/spam")
	public String spam(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		List<MailEntity> spam =  service.getAllSpamMails(getUserUsername());
		Collections.reverse(spam);
		model.addAttribute("spam",spam);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/mail/spam";
	}
	@GetMapping("/spam/{id}")
	public String setspam(RedirectAttributes redirAttrs,@PathVariable("id") int mailid) {
		MailEntity oldMail = mailRepo.findByMailId(mailid);
		oldMail.setSpam(1);
		mailRepo.save(oldMail);
		return "redirect:/student/spam";
	}
	
	@GetMapping("/composeMail")
	public String composeMail(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/mail/compose";
	}
	@PostMapping("/sendMail")
	public String sendMail(RedirectAttributes redirAttrs,@RequestParam("message") String body, @RequestParam("subject") String topic,
			@RequestParam("reciever") String reciever) {
		MailEntity mail = new MailEntity();
		mail.setBody(body);
		mail.setObject(topic);
		List <UserEntity> recievers = new ArrayList<>();
		recievers.add(userRepo.findByEmail(reciever));
		mail.setRecievers(recievers);
		mail.setSender(userRepo.findByEmail(getUserUsername()));
		service.SendMailUser(mail);
		System.out.println("Sending : "+getUserUsername()+" "+body+" "+topic+" to "+reciever);
        redirAttrs.addFlashAttribute("success", "Mail Sent !");
	    return "redirect:/student/composeMail";
	}
	@GetMapping("/tables")
	public String tables(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/tables";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/tables";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/tables";
	}
	@GetMapping("/my-profile")
	public String myprofile(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/my-profile";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/my-profile";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/profile";
	}
	/////////////////////////////
	@GetMapping("/my-account")
	public String myaccount(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/my-account";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/my-account";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "student/myaccount";
	}
	@PostMapping("/upd_account")
	public String EditAccount( Model model ,@RequestParam ("firstName") String firstName,@RequestParam ("lastName") String lastName,
			@RequestParam ("email") String email , @RequestParam("password") String password,
			@RequestParam("phone") String phone,/*@RequestParam ("birthDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date birthDate  ,*/@RequestParam("file") MultipartFile file, 
			RedirectAttributes redirAttrs) {
			UserEntity olduser =userRepo.findByEmail(email);
			UserEntity newuser =new UserEntity();
			String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
	    	if(FileName.contains("..")) {
	    		System.out.println("not a proper file ");
	    	}
	    	try {
	    		if(!FileName.isEmpty()) {
	    			newuser.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
					System.out.println("cv");
			
	    		}
	    		else {
	    			newuser.setImageU(olduser.getImageU());
	    		}
						} catch (IOException e) {
				System.out.println("dowiw");
				e.printStackTrace();
			}
		
			
	    	
	    	newuser.setEmail(email);
			UserEntity existingMail = userRepo.findByEmail(newuser.getEmail());
			
	        if((existingMail != null)&&(existingMail != olduser))
	        {	
	        	redirAttrs.addFlashAttribute("error", "Mail already exists");
	        	return "redirect:/student/my-profile";
	        }
	        
	        else
	        {
		       System.out.println("password = '"+password+"'");
		       newuser.setPassword(password);
				 newuser.setPhone(phone);
				 newuser.setFirstName(firstName);
				 newuser.setLastName(lastName);
				 //newuser.setBirthDate(birthDate);
				 service.modifyUserEntity(olduser.getId(), newuser);
				 
				 
				 
			 if (!newuser.getEmail().equals(olduser.getEmail())) {
				 ConfirmationToken confirmationToken = new ConfirmationToken(olduser);
		            conftrepo.save(confirmationToken);
		            String text="To confirm your email, please click here : "
		                    +"http://localhost:9090/confirm-Email/"+confirmationToken.getConfirmationToken()+"/"
		                    +newuser.getEmail();
		            SendEmailService.verifyEmail(newuser.getEmail(),"Mail Verified!",text);
		           redirAttrs.addFlashAttribute("success", "Email Changed! Check your mail to Verifie it");
		            return "redirect:/Login";
			 }
		return "redirect:/student/my-account";
		}
	}
	/////////////////////////////////
	@GetMapping("/timetable")
	public String timetable(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Classe classe =  user.getClasses().get(0);
		model.addAttribute("classe",classe);
	    return "student/timetable";
	}


	/**************clubs ****************/
		
		@GetMapping("/listclub")
		public String AllClubs(Model model) {
			
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Club> Clubs =  service.getAllClubs();
			model.addAttribute("clubs",Clubs);
			
		    return "student/listclub";
		}
		@GetMapping("/ContactClubs")
		public String ContactClubs(Model model) {
			
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Club> Clubs =  service.getAllClubs();
			model.addAttribute("clubs",Clubs);
			
		    return "student/ContactClub";
		}
		/**************Payments****************/
		@GetMapping("/payments")
		public String AllPayments(Model model) {
			String email = getUserUsername();
			UserEntity user = userRepo.findByEmail(email);
			model.addAttribute("user",user);
			List<Payement> payments =  user.getTransactions();
			model.addAttribute("payments",payments);
			float paid = service.getAllByPaid(email);
			model.addAttribute("paid",paid);
			float nonpaid = service.getAllNonPaid(email);
			model.addAttribute("nonpaid",nonpaid);
		    return "student/listPayments";
		}
		//////////Documents///////////////
		@GetMapping("/subjects")
		public String listSubjects(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			
			List<Subject> subjects = user.getClasses().get(0).getSection().getSubjects();
			model.addAttribute("subjects",subjects);
		    return "student/subjects";
		}
		@GetMapping("/listDocs/{idsubject}")
		public String listDocs(Model model,
				@PathVariable("idsubject") long idsubject) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Document> docs=new ArrayList<>();
			for (Document doc : docrepo.findAll()) {
				if((doc.getClasse()==user.getClasses().get(0))&&(doc.getSubject().getSubjectId()==idsubject)
						&&(doc.getProf().getRole().equalsIgnoreCase("PROF")))
					docs.add(doc);
			}
			model.addAttribute("docs",docs);
		    return "student/listDocs";
		}
		
		@GetMapping("/downloadDoc/{docId}")
		public ResponseEntity<ByteArrayResource> downloadDoc(@PathVariable("docId") long docId) {
			Document doc = service.getDocById(docId).get();
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(doc.getDocType()))
					.header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+doc.getDocName()+"\"")
					.body(new ByteArrayResource(doc.getData()));
		}
		@GetMapping("/listDocs")
		public String listDocs(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Document> docs = new ArrayList<>();
			for (Document document : docrepo.findAll()) {
				if(document.getProf().getRole().equalsIgnoreCase("STUDENT"))
					docs.add(document);
			}
			model.addAttribute("docs",docs);
		    return "student/listSharedDocs";
		}
		@GetMapping("/uploadDoc")
		public String uploadDoc(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Classe> classes = service.getAllClasses();
			model.addAttribute("classes",classes);
			List<Subject> Subjects = service.getAllSubjects();
			model.addAttribute("subjects",Subjects);

		    return "student/addDoc";
		}
		@PostMapping("/uploadDocument")
		public String uploadDoc1(@RequestParam("files") MultipartFile[] files,
				@RequestParam("subject") String subject)
		{
			for (MultipartFile file:files) {
				UserEntity user = userRepo.findByEmail(getUserUsername());
				service.createDoc(file, user.getClasses().get(0)	, user, subjectRepo.findBySubjectName(subject));
			}
			
			return "redirect:/student/listDocs";
			}
		
		///////////////Requests///////////////////
		@GetMapping("/request")
		public String Request(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			return "student/request" ;
			
		}
		@PostMapping("/request")
		public String request(@RequestParam("subject") String subject,
				@RequestParam("message") String message,RedirectAttributes redirAttrs) {
		
			
		Request request = new Request();
		request.setMessage(message);
		request.setSubject(subject);
		request.setStudentEmail(getUserUsername());
		service.createRequest(request);
        redirAttrs.addFlashAttribute("success", "Request Sent !");
			return "redirect:/student/request";
			}
		//////////////////////////////////
		@GetMapping("/groups")
		public String myGroups(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			return "student/groups" ;
			
		}
		@GetMapping("/notes")
		public String myNotes(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			return "student/MyNotes" ;
		}
		@GetMapping("/absences")
		public String myAbsences(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			return "student/myAbsences" ;
			
		}
		@GetMapping("/certifications")
		public String AllCertifications(Model model) {
			UserEntity user = userRepo.findByEmail(getUserUsername());
			model.addAttribute("user",user);
			List<Certification> Certifications =  service.getAllCertification();
			model.addAttribute("certifications",Certifications);
			
		    return "student/certifications";
		}
}
