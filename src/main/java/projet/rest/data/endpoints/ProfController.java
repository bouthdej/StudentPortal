package projet.rest.data.endpoints;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
import projet.rest.data.models.Classe;
import projet.rest.data.models.Club;
import projet.rest.data.models.Document;
import projet.rest.data.models.MailEntity;
import projet.rest.data.models.Seance;
import projet.rest.data.models.Subject;
import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.ClasseRepository;
import projet.rest.data.repositories.DocumentRepository;
import projet.rest.data.repositories.MailRepository;
import projet.rest.data.repositories.SubjectRepository;
import projet.rest.data.repositories.UserRepository;
import projet.rest.data.services.SendEmailService;
import projet.rest.data.services.UserService;

@Controller
@Data
@AllArgsConstructor
@RequestMapping("/prof")
public class ProfController {
	@Autowired
	private UserService service ;
	private UserRepository userRepo;	
	private MailRepository mailRepo;
	private ClasseRepository classRepo;
	private DocumentRepository docRepo;
	private SubjectRepository subjectRepo;
	@Autowired
	SendEmailService SendEmailService;
	
	public String CheckRole () {
		Collection<? extends GrantedAuthority> authorities;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    authorities = auth.getAuthorities();
	     
	    return authorities.toArray()[0].toString();
	}
	public String getUserUsername() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username ;
		if (principal instanceof UserDetails) {
		 username = ((UserDetails)principal).getUsername();
		} else {
		 username = principal.toString();
		}
		return username;
	}
	
	@GetMapping("/home")
	public String profindex(Model model) {
		System.out.println("-------------"+getUserUsername());
		if (userRepo.findByEmail(getUserUsername()).getVerified()==0) {
			return "redirect:/confirm-account/"+getUserUsername();			
		}
		else if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List <Club> clubs = service.getAllClubs();
		model.addAttribute("clubs",clubs);
		String day = LocalDate.now().getDayOfWeek().name();
		List <Seance> seances = new ArrayList<>();
		for (Seance s : user.getSeances()) {
			if(s.getDayOfWeek().equalsIgnoreCase(day)) {
				seances.add(s);
			}
		}
		model.addAttribute("day",day);
		model.addAttribute("seances",seances);
		List <Announce> Allans = service.getAllAnnounce();
		List <Announce> ans	= Allans.subList(Math.max(Allans.size() - 3, 0), Allans.size());
		model.addAttribute("ans",ans);
	    return "prof/profindex";
	}
	@GetMapping("/liststudent")
	public String listStudent(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		
		
	    return "prof/liststudent";
	}
	////////////////Mail//////////////////
	@GetMapping("/inbox")
	public String inbox(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		
		List<MailEntity> recieved =  service.getAllRecievedMails(getUserUsername());
			
		model.addAttribute("recieved",recieved);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "prof/mail/inbox";
	}
	@GetMapping("/sent")
	public String sent(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/sent";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		List<MailEntity> sent =  service.getAllSentMails(getUserUsername());
		model.addAttribute("sent",sent);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "prof/mail/sent";
	}
	@GetMapping("/spam")
	public String spam(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/spam";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		List<MailEntity> spam =  service.getAllSpamMails(getUserUsername());
		model.addAttribute("spam",spam);
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "prof/mail/spam";
	}
	@PostMapping("/spam")
	public String setspam(RedirectAttributes redirAttrs,@PathVariable("id") int mailid) {
		MailEntity oldMail = mailRepo.findByMailId(mailid);
		oldMail.setSpam(1);
		mailRepo.save(oldMail);
		//<form method="POST" th:action="@{/student/spam/{id}(id=${mail.mailid})}">
		return "redirect:/prof/inbox";
	}
	
	@GetMapping("/composeMail")
	public String composeMail(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = classRepo.findAll();
		model.addAttribute("classes",classes);
	    return "prof/mail/compose";
	}
	@PostMapping("/sendMail")
	public String sendMail(RedirectAttributes redirAttrs,@RequestParam("message") String body, @RequestParam("subject") String topic,
			@RequestParam("reciever") String reciever , @RequestParam("Classn") String className) {
		MailEntity mail = new MailEntity();
		UserEntity user = userRepo.findByEmail(reciever);
		Classe classe = classRepo.findByClassName(className);
		if ((user==null)&&(classe==null)) {
			redirAttrs.addFlashAttribute("error", "You need to specify at least a class or a mail receiver");
		    return "redirect:/prof/composeMail";
		}
		else if ((user!=null)&&(classe==null)) {
			mail.setBody(body);
			mail.setObject(topic);
			List <UserEntity> recievers = new ArrayList<>();
			recievers.add(userRepo.findByEmail(reciever));
			mail.setRecievers(recievers);
			mail.setSender(userRepo.findByEmail(getUserUsername()));
			service.SendMailUser(mail);
			System.out.println("Sending : "+getUserUsername()+" "+body+" "+topic+" to "+reciever);
	        redirAttrs.addFlashAttribute("success", "Mail Sent !");
		    return "redirect:/prof/composeMail";
		}
		else if ((user==null)&&(classe!=null)) {
			mail.setBody(body);
			mail.setObject(topic);
			List <UserEntity> students = new ArrayList<>();
			for (UserEntity userEntity : classe.getUsers()) {
				if(userEntity.getRole().equalsIgnoreCase("STUDENT"))
					students.add(userEntity);
			}
			System.out.println("test 0");
			mail.setRecievers(students);
			mail.setSender(userRepo.findByEmail(getUserUsername()));
			System.out.println("test 1");
			service.SendMailToClass(mail);
			System.out.println("Sending : "+getUserUsername()+" "+body+" "+topic+" to "+className);
	        redirAttrs.addFlashAttribute("success", "Mail Sent !");
		    return "redirect:/prof/composeMail";
		}
		else {
			mail.setBody(body);
			mail.setObject(topic);
			List <UserEntity> students = new ArrayList<>();
			for (UserEntity userEntity : classe.getUsers()) {
				if(userEntity.getRole().equalsIgnoreCase("STUDENT"))
					students.add(userEntity);
			}
			students.add(userRepo.findByEmail(reciever));
			mail.setRecievers(students);
			mail.setSender(userRepo.findByEmail(getUserUsername()));
			service.SendMailToClass(mail);
			System.out.println("Sending : "+getUserUsername()+" "+body+" "+topic+" to "+reciever + " and  to class "+className);
	        redirAttrs.addFlashAttribute("success", "Mail Sent !");
		    return "redirect:/prof/composeMail";
		}
		
	}	
	
	///////////////////////////////////////////////////
	
	//////////Documents///////////////
	@GetMapping("/listDocs")
	public String listDocs(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		
		List<Document> docs = new ArrayList<>();
		for (Document doc : docRepo.findAll()) {
			if(doc.getProf().getRole().equalsIgnoreCase("PROF"))
				docs.add(doc);
		}
		model.addAttribute("docs",docs);
	    return "prof/listDocs";
	}
	@GetMapping("/uploadDocument")
	public String uploadDoc(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		List<Subject> Subjects = service.getAllSubjects();
		model.addAttribute("subjects",Subjects);

	    return "prof/addDoc";
	}
	@PostMapping("/uploadDocument")
	public String uploadDoc(@RequestParam("files") MultipartFile[] files,
			@RequestParam("classe") String classe,
			@RequestParam("subject") String subject)
	{
		for (MultipartFile file:files) {
			System.out.println("$$$$$$$$$$$"+file.getOriginalFilename());
			service.createDoc(file, classRepo.findByClassName(classe), userRepo.findByEmail(getUserUsername()), subjectRepo.findBySubjectName(subject));
		}
		
		return "redirect:/prof/listDocs";
		}
	@GetMapping("/downloadDoc/{docId}")
	public ResponseEntity<ByteArrayResource> downloadDoc(@PathVariable("docId") long docId) {
		Document doc = service.getDocById(docId).get();
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(doc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename=\""+doc.getDocName()+"\"")
				.body(new ByteArrayResource(doc.getData()));
	}
}
