package projet.rest.data.endpoints;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.ModelAttribute;
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
import projet.rest.data.models.Payement;
import projet.rest.data.models.Request;
import projet.rest.data.models.Seance;
import projet.rest.data.models.Section;
import projet.rest.data.models.Subject;
import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.CertificationRepository;
import projet.rest.data.repositories.ClasseRepository;
import projet.rest.data.repositories.ClubRepository;
import projet.rest.data.repositories.ConfirmationTokenRepository;
import projet.rest.data.repositories.DocumentRepository;
import projet.rest.data.repositories.RequestRepository;
import projet.rest.data.repositories.SeanceRepository;
import projet.rest.data.repositories.SectionRepository;
import projet.rest.data.repositories.SubjectRepository;
import projet.rest.data.repositories.UserRepository;
import projet.rest.data.services.SendEmailService;
import projet.rest.data.services.UserService;

@Controller
@Data
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	UserService service ;
	
	private UserRepository userRepo;
	private ConfirmationTokenRepository conftrepo;
	private ClubRepository clubrepo;
	private SectionRepository sectionrepo;
	private SubjectRepository subjectrepo;
	private ClasseRepository classerepo;
	private SeanceRepository seancerepo;
	private CertificationRepository certRepo; 
	private RequestRepository reqRepo; 
	private DocumentRepository docrepo;
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
	public String returnindexadmin(Model model) {
		if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "admin/adminindex";
	}
	
	/*********************Student**********************/
	
	@GetMapping("/studentlist")
	public String AllStudent(Model model) {
		List<UserEntity> users =  service.getUserByRole("STUDENT");
		model.addAttribute("users",users);
		UserEntity user = new UserEntity();
		model.addAttribute("user",user);
		
	    return "admin/studentlist";
	}
	
	@GetMapping("/delstudent/{id}")
	public String DelStudent(@PathVariable("id") Long id, Model model) {
	    service.DelTokenByIdUser(id);
	    service.deleteUserEntity(id);
		return "redirect:/admin/studentlist";
	}

	@GetMapping("/addstudent")
	public String AddStudent(Model model) {
		UserEntity user = new UserEntity();
		model.addAttribute("user",user);
	    return "admin/addstudent";
	}
	
	@PostMapping("/addstudent")
	public String StudentregisterSuccess(@ModelAttribute("user") UserEntity user,RedirectAttributes redirAttrs, Model model,@RequestParam("file")MultipartFile file ) {
		String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
    	if(FileName.contains("..")) {
    		System.out.println("not a proper file ");
    	}
    	try {
			user.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
			System.out.println("cv");
		} catch (IOException e) {
			System.out.println("dowiw");
			e.printStackTrace();
		}
    	UserEntity existingMail = userRepo.findByEmail(user.getEmail());
        if(existingMail != null)
        {	redirAttrs.addFlashAttribute("error", "mail already exists");
        	return "redirect:/admin/addstudent";
        }
        else
        {
        	user.setRole("STUDENT");
        	service.createUserEntity(user);
         //  redirAttrs.addFlashAttribute("success", "Account created! Check your mail to activate Your Account");
            return "redirect:/admin/studentlist";
  }
		
	}
	
	@GetMapping("/updstudent/{id}")
	public String UpdStudent(@PathVariable("id") int id, Model model) {
		UserEntity user = service.getUserEntityById(id);
		model.addAttribute("user", user);
	    return "admin/updstudent";
	}
	@PostMapping("/updstudent/{id}")
	public String EditSuucesStudent( Model model ,@PathVariable("id") long id ,@RequestParam ("firstName") String firstName,@RequestParam ("lastName") String lastName , @RequestParam ("email") String email , @RequestParam("password") String password, @RequestParam("phone") String phone,/*@RequestParam ("birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime birthDate,*/  @RequestParam("file") MultipartFile file  ) {
		 UserEntity user =new UserEntity();
		 user.setFirstName(firstName);
		 user.setLastName(lastName);
		 user.setEmail(email);
		 user.setPassword(password);
		 user.setPhone(phone);
		 //user.setBirthDate(birthDate);
		 
		 String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
	    	if(FileName.contains("..")) {
	    		System.out.println("not a proper file ");
	    	}
	    	try {
	    		if(!FileName.isEmpty())
	    			user.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
				else 
					user.setImageU(service.getUserEntityById(id).getImageU());
				
				System.out.println("cv");
			} catch (IOException e) {
				System.out.println("dowiw");
				e.printStackTrace();
			}
		 service.modifyUserEntity(id, user);
		 return "redirect:/admin/studentlist"; 
	}
	
	
	/*********************Professor**********************/
	
	@GetMapping("/proflist")
	public String AllProf(Model model) {
		List<UserEntity> users =  service.getUserByRole("PROF");
		model.addAttribute("users",users);
		UserEntity user = new UserEntity();
		model.addAttribute("user",user);
		
	    return "admin/proflist";
	}
	@GetMapping("/delprof/{id}")
	public String DelProf(@PathVariable("id") Long id, Model model) {
		service.deleteUserEntity(id);
		return "redirect:/admin/proflist";
	}
	@GetMapping("/addprof")
	public String AddProf(Model model) {
		UserEntity user = new UserEntity();
		model.addAttribute("user",user);
	    return "admin/addprof";
	}
	
	@PostMapping("/addprof")
	public String ProfregisterSuccess(@ModelAttribute("user") UserEntity user,RedirectAttributes redirAttrs, Model model,@RequestParam("file")MultipartFile file ) {
		String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
    	if(FileName.contains("..")) {
    		System.out.println("not a proper file ");
    	}
    	try {
			user.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
			System.out.println("cv");
		} catch (IOException e) {
			System.out.println("dowiw");
			e.printStackTrace();
		}
    	UserEntity existingMail = userRepo.findByEmail(user.getEmail());
        if(existingMail != null)
        {	redirAttrs.addFlashAttribute("error", "mail already exists");
        	return "redirect:/admin/addprof";
        }
        else
        {
        	user.setRole("PROF");
        	service.createUserEntity(user);
           redirAttrs.addFlashAttribute("success", "Account created! Check your mail to activate Your Account");
            return "redirect:/admin/proflist";
        }
    	
	}
	
	@GetMapping("/updprof/{id}")
	public String UpdProf(@PathVariable("id") int id, Model model) {
		UserEntity user = service.getUserEntityById(id);
		model.addAttribute("user", user);
	    return "admin/updprof";
	}
	@PostMapping("/updprof/{id}")
	public String EditSuucesProf( Model model ,@PathVariable("id") long id ,@RequestParam ("firstName") String firstName,@RequestParam ("lastName") String lastName , @RequestParam ("email") String email , @RequestParam("password") String password, @RequestParam("phone") String phone,/*@RequestParam ("birthDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime birthDate,*/  @RequestParam("file") MultipartFile file  ) {
		 UserEntity user =new UserEntity();
		 user.setFirstName(firstName);
		 user.setLastName(lastName);
		 user.setEmail(email);
		 user.setPassword(password);
		 user.setPhone(phone);
		 //user.setBirthDate(birthDate);
		 
		 String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
	    	if(FileName.contains("..")) {
	    		System.out.println("not a proper file ");
	    	}
	    	try {
	    		if(!FileName.isEmpty())
	    			user.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
				else 
					user.setImageU(service.getUserEntityById(id).getImageU());
				
				System.out.println("cv");
			} catch (IOException e) {
				System.out.println("dowiw");
				e.printStackTrace();
			}
		
		 
		 service.modifyUserEntity(id, user);
		 return "redirect:/admin/proflist"; 
	}
	
	/*********************Section**********************/
	
	@GetMapping("/sectionlist")
	public String AllSections(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Section> sections =  service.getAllSection();
		model.addAttribute("sections",sections);
		Section section = new Section();
		model.addAttribute("section",section);
		
	    return "admin/sectionlist";
	}
	
	@GetMapping("/addsection")
	public String AddSection(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Section section = new Section();
		model.addAttribute("section",section);
	    return "admin/addsection";
	}
	
	@PostMapping("/addsection")
	public String AddSectionScc(@ModelAttribute("section") Section section, Model model,@RequestParam("SectionName") String SectionName) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		section.setSectionName(SectionName);
		service.createSection(section, SectionName);

        return "redirect:/admin/sectionlist";
    }
    
	@GetMapping("/delsection/{id}")
	public String DelSection(@PathVariable("id") Long id, Model model) {
		service.deleteSection(id);
		return "redirect:/admin/sectionlist";
	}
	/*********************Club**********************/
	@GetMapping("/clublist")
	public String AllClubs(Model model) {
		
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Club> Clubs =  service.getAllClubs();
		model.addAttribute("clubs",Clubs);
		
	    return "admin/clublist";
	}
	@GetMapping("/addclub")
	public String AddClub(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);	
		Club club = new Club();
		model.addAttribute("club",club);
	    return "admin/addclub";
	}
	
	@PostMapping("/addclub")
	public String ClubregisterSuccess(@ModelAttribute("club") Club club,RedirectAttributes redirAttrs, Model model,@RequestParam("file")MultipartFile file,
			@RequestParam("ClubOwner") String ClubOwner,@RequestParam("ClubName") String ClubName) {
		String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
    	if(FileName.contains("..")) {
    		System.out.println("not a proper file ");
    	}
    	try {
    		club.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
			System.out.println("cv");
		} catch (IOException e) {
			System.out.println("dowiw");
			e.printStackTrace();
		}
    		//club.setClubName(ClubName);
    		//club.setClubOwner(userRepo.findByEmail(ClubOwner));
    		if(userRepo.findByEmail(club.getClubOwner())==null) {
    			redirAttrs.addFlashAttribute("error", "Mail not found!");
    		return "redirect:/admin/addclub";}
    		//System.out.println("$$$$$$$$"+ClubOwner);
    		//System.out.println("$$$$$$$$"+club.getClubOwner().getEmail());

    		service.createClub(club);
           redirAttrs.addFlashAttribute("success", "Club created!");
            return "redirect:/admin/clublist";
	}
	@GetMapping("/delclub/{id}")
	public String DelClub(@PathVariable("id") Long id, Model model) {
		service.deleteClub(id);
		return "redirect:/admin/clublist";
	}
	
		/*********************Subject**********************/
	
	@GetMapping("/subjectlist")
	public String AllSubjects(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Subject> subjects =  service.getAllSubjects();
		model.addAttribute("subjects",subjects);
		for (Subject subject : subjects) {
			System.out.println("$$$$$$$$$$"+subject.getSubjectName()+subject.getCoefficient());
		}
	    return "admin/subjectlist";
	}
	
	@GetMapping("/addsubject")
	public String AddSubject(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Subject subject = new Subject();
		model.addAttribute("subject",subject);
	    return "admin/addsubject";
	}
	
	@PostMapping("/addsubject")
	public String AddSubject(@ModelAttribute("subject") Subject subject, Model model,RedirectAttributes redirAttrs) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		if (subjectrepo.findBySubjectName(subject.getSubjectName())!=null) {
			redirAttrs.addFlashAttribute("error", "Subject already exists");
			return "redirect:/admin/addsubject";
		}
		service.createSubject(subject);

        return "redirect:/admin/subjectlist";
    }
    
	@GetMapping("/delsubject/{id}")
	public String DelSubject(@PathVariable("id") Long id, Model model) {
		service.deleteSubject(id);
		return "redirect:/admin/subjectlist";
	}
	/*********************Classe**********************/

	@GetMapping("/classelist")
	public String AllClasses(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes =  service.getAllClasses();
		model.addAttribute("classes",classes);
		Classe classe = new Classe();
		model.addAttribute("classe",classe);
		
	    return "admin/classelist";
	}
	@GetMapping("/addclasse")
	public String AddClasse(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Section> sections = service.getAllSection();
		model.addAttribute("sections",sections);
		Section section = new Section();
		model.addAttribute("section",section);
		Classe classe = new Classe();
		model.addAttribute("classe",classe);
	    return "admin/addclasse";
	}
	
	@PostMapping("/addclasse")
	public String ClasseRegisterSuccess(@ModelAttribute("classe") Classe classe, Model model
			,@RequestParam ("sectionn") String sectionName) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		System.out.println("$$$$$$"+classe.getSection());
		classe.setSection(sectionrepo.findBySectionName(sectionName));
		service.createClasse(classe);

        return "redirect:/admin/classelist";
    }
	/*********************Classe**********************/
	@GetMapping("/listSeanceToClass")
	public String AllSeanceToClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes =  service.getAllClasses();
		model.addAttribute("classes",classes);
		/*for (Section section : sections) {
			for (Subject subject : section.getSubjects()) {
				System.out.println("$$$$$"+section.getSectionName()+" "+subject.getSubjectName());
			}
		}*/
	    return "admin/listSeanceToClass";
	}
	@GetMapping("/addSeanceToClass")
	public String AddSeanceToClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		Seance seance = new Seance();
		model.addAttribute("seance",seance);
	    return "admin/addSeanceToClass";
	}
	
	@PostMapping("/addSeanceToClass")
	public String AddSeanceToClass(@ModelAttribute("seance") Seance seance, Model model,
			@RequestParam ("Classn") String className,@RequestParam ("email") String email,RedirectAttributes redirAttrs) {
		
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		UserEntity prof = userRepo.findByEmail(email);
		Classe classe = classerepo.findByClassName(className);
		System.out.println("$$$$$"+prof.getFirstName());
		
		
		if(prof==null) {
			redirAttrs.addFlashAttribute("error", "Email not available!");
			return "redirect:/admin/addSeanceToClass";
		}
		else if(!prof.getRole().equalsIgnoreCase("PROF")) {
			redirAttrs.addFlashAttribute("error", "This Email is not a prof email!");
			return "redirect:/admin/addSeanceToClass";
		}
		else if(classe==null) {
			redirAttrs.addFlashAttribute("error", "Classe not available!");
			return "redirect:/admin/addSeanceToClass";
		}
		else {
			
		
		List<Seance> profSeances = prof.getSeances();

		for (Seance s : profSeances) {
			if((s.getDayOfWeek().equalsIgnoreCase(seance.getDayOfWeek()))&& (s.getNumSeance()==seance.getNumSeance())) {
				redirAttrs.addFlashAttribute("error", "Proffesor Have other plans for this time!");
				return "redirect:/admin/addSeanceToClass";
			}
		}
		//System.out.println("$$$$$$"+classe.getSection());
		List<Seance> AllSeances = classe.getSeances();
		for (Seance s : AllSeances) {
			if((s.getDayOfWeek().equalsIgnoreCase(seance.getDayOfWeek()))&& (s.getNumSeance()==seance.getNumSeance())) {
				redirAttrs.addFlashAttribute("error", "Seance Already planned!");
				return "redirect:/admin/addSeanceToClass";
			}
			else if((s.getDayOfWeek().equalsIgnoreCase(seance.getDayOfWeek()))&& (s.getNumSeance()==seance.getNumSeance())&& (s.getNumClass()==seance.getNumClass())){
				redirAttrs.addFlashAttribute("error", "Classroom Already reserved!");
				return "redirect:/admin/addSeanceToClass";
			}
		}
		
		profSeances.add(seance);
		prof.setSeances(profSeances);
		
		AllSeances.add(seance);
		classe.setSeances(AllSeances);
		
		seance.setClasse(classe);
		seance.setProf(prof);
		
		userRepo.save(prof);
		classerepo.save(classe);
		service.createSeance(seance);
		for (UserEntity p : userRepo.findAll()) {
			if (p.getRole().equalsIgnoreCase("PROF")) {
			for (Seance s : user.getSeances()) {
				System.out.println("$$$$$"+s.getSubjectName());
			}			}
		}
		redirAttrs.addFlashAttribute("success", "Seance Planned Successfully!");
        return "redirect:/admin/addSeanceToClass";
}}
	@GetMapping("/delSeanceFromClass/{idclass}/{idseance}")
	public String DelSeanceFromeClass(@PathVariable("idclass") Long idclass,
			@PathVariable("idseance") Long idseance, Model model,RedirectAttributes redirAttrs) {
		Classe classe =service.getClasseById(idclass);
		List<Seance> classSeances = classe.getSeances();
		Seance seance = service.getSeanceById(idseance);
		
		UserEntity prof = seance.getProf();
		List<Seance> profSeances = prof.getSeances();
		
		classSeances.remove(seance);
		profSeances.remove(seance);
		service.deleteSeance(idseance);
		
		prof.setSeances(profSeances);
		classe.setSeances(classSeances);

		userRepo.save(prof);
		classerepo.save(classe);
		redirAttrs.addFlashAttribute("Success", "Seance deleted from Class");
		return "redirect:/admin/listSeanceToClass";
	}
	/*********************SectionSubject**********************/

	@GetMapping("/listSectionSubject")
	public String AllSectionSubject(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Section> sections =  service.getAllSection();
		model.addAttribute("sections",sections);
		for (Section section : sections) {
			for (Subject subject : section.getSubjects()) {
				System.out.println("$$$$$"+section.getSectionName()+" "+subject.getSubjectName());
			}
		}
	    return "admin/listSectionSubject";
	}
	@GetMapping("/addSubjectToSection")
	public String AddSubjectToSection(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Section> sections = service.getAllSection();
		List<Subject> subjects = service.getAllSubjects();
		model.addAttribute("sections",sections);
		model.addAttribute("subjects",subjects);
		
	    return "admin/addsubjectTosession";
	}
	
	@PostMapping("/addSubjectToSection")
	public String AddSubjectToSection(@ModelAttribute("section") Section section, Model model,
			@RequestParam ("sectionn") String sectionName,@RequestParam ("subjectn") String subjectName,RedirectAttributes redirAttrs) {
		
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Section sect = sectionrepo.findBySectionName(sectionName);
		Subject subj = subjectrepo.findBySubjectName(subjectName);
		List<Subject> subjs = sect.getSubjects();
		for (Subject subject : subjs) {
			if(subj.getSubjectName().equalsIgnoreCase(subject.getSubjectName())) {
				redirAttrs.addFlashAttribute("error", "Subject already exists in that section");
				return "redirect:/admin/addSubjectToSection";
			}
		}
		List<Section> sects = subj.getSections();
		subjs.add(subj);
		sects.add(sect);
		sect.setSubjects(subjs);
		subj.setSections(sects);
		sectionrepo.save(sect);
		subjectrepo.save(subj);
        return "redirect:/admin/listSectionSubject";
    }
	@GetMapping("/delSubjectFromSection/{idsection}/{idsubject}")
	public String DelSubjectFromSection(@PathVariable("idsection") Long idsection,
			@PathVariable("idsubject") Long idsubject, Model model,RedirectAttributes redirAttrs) {
		Section section = service.getSectionById(idsection);
		List<Subject> subjects = section.getSubjects();
		Subject subject = service.getSubjectById(idsubject);
		List<Section> SubjectSection = subject.getSections();
		subjects.remove(subject);
		SubjectSection.remove(section);
		subject.setSections(SubjectSection);
		section.setSubjects(subjects);
		sectionrepo.save(section);
		subjectrepo.save(subject);
		redirAttrs.addFlashAttribute("Success", "Subject deleted from Section");
		return "redirect:/admin/listSectionSubject";
	}
	
	//////////
	
	/*********************StudentClass**********************/

	@GetMapping("/listStudentClass")
	public String AllStudentClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes =  service.getAllClasses();
		model.addAttribute("classes",classes);
		
	    return "admin/listStudentClass";
	}
	@GetMapping("/addStudentToClass")
	public String AddStudentToClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		
	    return "admin/addStudentToClass";
	}
	
	@PostMapping("/addStudentToClass")
	public String AddStudentToClass(@RequestParam ("email") String email,
			@RequestParam ("Classn") String className,
			@RequestParam ("gender") String gender,
			RedirectAttributes redirAttrs) {
		
		UserEntity user = userRepo.findByEmail(email);
		Classe clas = classerepo.findByClassName(className);
		if(clas==null) {
			redirAttrs.addFlashAttribute("error", "You must choose a class");
			return "redirect:/admin/addProfToClass";
		}
		List<UserEntity> students = clas.getUsers();
		if(user==null) {
			redirAttrs.addFlashAttribute("error", "Email Does Not Exist");
			return "redirect:/admin/addStudentToClass";
		}
		else if(user.getRole().equalsIgnoreCase("STUDENT")) {
		for (UserEntity e : students) {
				if(e.getEmail().equalsIgnoreCase(email)) {
					redirAttrs.addFlashAttribute("error", "Student already exists in this Class");
					return "redirect:/admin/addStudentToClass";
				}
			}
		List<Classe> oldClass = user.getClasses();
		for (Classe classe : oldClass) {
			classe.getUsers().remove(user);
		}
		List<Classe> a = user.getClasses();
		a.clear();
		a.add(clas);
		user.setGender(gender);
		user.setClasses(a);
		students.add(user);
		clas.setUsers(students);
		userRepo.save(user);
		classerepo.save(clas);
		return "redirect:/admin/listStudentClass";
			}
		else if (user.getRole().equalsIgnoreCase("PROF")) {
			redirAttrs.addFlashAttribute("error", "This is a Professor email! not a Student");
			return "redirect:/admin/addStudentToClass";
			}
		return "Other/404";
    }
	@GetMapping("/delStudentFromClass/{idclass}/{idstudent}")
	public String DelStudentFromeClass(@PathVariable("idclass") Long idclass,
			@PathVariable("idstudent") Long idstudent, Model model,RedirectAttributes redirAttrs) {
		Classe classe = service.getClasseById(idclass);
		List<UserEntity> students = classe.getUsers();
		UserEntity student = service.getUserEntityById(idstudent);
		List<Classe> Userclasse = student.getClasses();
		students.remove(student);
		Userclasse.remove(classe);
		student.setClasses(Userclasse);
		classe.setUsers(students);
		userRepo.save(student);
		classerepo.save(classe);
		redirAttrs.addFlashAttribute("Success", "Student deleted from Class");
		return "redirect:/admin/listStudentClass";
	}
	/*********************ProfClass**********************/

	@GetMapping("/listProfClass")
	public String AllProfClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes =  service.getAllClasses();
		model.addAttribute("classes",classes);
		/*for (Classe clas : classes) {
			for (UserEntity userr : clas.getUsers()) {
				if(userr.getRole().equalsIgnoreCase("prof"))
				System.out.println("$$$$$"+clas.getClassName()+" "+user.getFirstName());
			}
		}*/
	    return "admin/listProfClass";
	}
	@GetMapping("/addProfToClass")
	public String AddProfToClass(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		
	    return "admin/addProfToClass";
	}
	
	@PostMapping("/addProfToClass")
	public String AddProfToClass(@RequestParam ("email") String email,
			@RequestParam ("Classn") String className,RedirectAttributes redirAttrs) {
		
		UserEntity user = userRepo.findByEmail(email);
		Classe clas = classerepo.findByClassName(className);
		if(clas==null) {
			redirAttrs.addFlashAttribute("error", "You must choose a class");
			return "redirect:/admin/addProfToClass";
		}
		List<UserEntity> students = clas.getUsers();
		if(user==null) {
			redirAttrs.addFlashAttribute("error", "Email Does Not Exist");
			return "redirect:/admin/addProfToClass";
		}
		else if(user.getRole().equalsIgnoreCase("Prof")) {
		for (UserEntity e : students) {
				if(e.getEmail().equalsIgnoreCase(email)) {
					redirAttrs.addFlashAttribute("error", "Prof already exists in this Class");
					return "redirect:/admin/addProfToClass";
				}
			}
		List<Classe> a = user.getClasses();
		a.add(clas);
		user.setClasses(a);
		students.add(user);
		clas.setUsers(students);
		userRepo.save(user);
		classerepo.save(clas);
		return "redirect:/admin/listProfClass";
			}
		else if (user.getRole().equalsIgnoreCase("Student")) {
			redirAttrs.addFlashAttribute("error", "This is a Student email! not a Professor");
			return "redirect:/admin/addProfToClass";
		//<span th:if="${teacher.gender == 'F'}">Female</span>
			}
		
		return "Other/404";
    }
	@GetMapping("/delProfFromClass/{idclass}/{idprof}")
	public String DelProfFromClass(@PathVariable("idclass") Long idclass,
			@PathVariable("idprof") Long idprof, Model model,RedirectAttributes redirAttrs) {
		Classe classe = service.getClasseById(idclass);
		List<UserEntity> profs = classe.getUsers();
		UserEntity prof = service.getUserEntityById(idprof);
		List<Classe> Userclasse = prof.getClasses();
		profs.remove(prof);
		Userclasse.remove(classe);
		prof.setClasses(Userclasse);
		classe.setUsers(profs);
		userRepo.save(prof);
		classerepo.save(classe);
		redirAttrs.addFlashAttribute("Success", "Prof deleted from Class");
		return "redirect:/admin/listProfClass";
	}
	////////////////Payement//////////////////
	@GetMapping("/addPayment")
	public String AddPayement(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		
		List<Classe> classes = service.getAllClasses();
		model.addAttribute("classes",classes);
		Payement payement = new Payement();
		model.addAttribute("payement",payement);
	    return "admin/addPayement";
	}
	@PostMapping("/addPayment")
	public String AddPayement(@RequestParam ("email") String email,
			@RequestParam ("Banque") String Banque,
			@RequestParam ("numeroCheque") String numeroCheque,
			@ModelAttribute("payement") Payement payement,
			RedirectAttributes redirAttrs) {
		
		UserEntity user = userRepo.findByEmail(email);

		
		if(user==null) {
			redirAttrs.addFlashAttribute("error", "Email Does Not Exist");
			return "redirect:/admin/addPayment";
		}
		else if(user.getRole().equalsIgnoreCase("Prof")) {
			redirAttrs.addFlashAttribute("error", "This is a Prof mail! Not a Student");
			return "redirect:/admin/addPayment";
			}
		else {
			
			payement.setEtudiant(user);
			if(payement.getModePayement().equalsIgnoreCase("Check")) {
				if(numeroCheque.equals("")) {
					redirAttrs.addFlashAttribute("error", "You must add a Check Number for this Payment mode");
					return "redirect:/admin/addPayment";
				}
				payement.setNumeroCheque(numeroCheque);
				payement.setBanque("null");
			}
			else if(payement.getModePayement().equalsIgnoreCase("transfer")) {
				if(Banque.equals("")) {
					redirAttrs.addFlashAttribute("error", "You must add a Bank Name for this Payment mode");
					return "redirect:/admin/addPayment";
				}
				payement.setBanque(Banque);
				payement.setNumeroCheque("null");
			}

			else{
				payement.setBanque("null");
				payement.setNumeroCheque("null");
			}
		List<Payement> pays = user.getTransactions();
		pays.add(payement);
		user.setTransactions(pays);
		service.createPayement(payement);
		userRepo.save(user);
		 redirAttrs.addFlashAttribute("success", "Transaction Added");
		return "redirect:/admin/addPayment";
		}
		//<span th:if="${teacher.gender == 'F'}">Female</span>
			}
	/**************Payments****************/
	@GetMapping("/listPayments")
	public String AllPayments(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Payement> payments =  service.getAllPayements();
		model.addAttribute("payments",payments);
		
	    return "admin/listPayments";
	}
	
	/*********************Certifications**********************/

	@GetMapping("/certifications")
	public String AllCertifications(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Certification> Certifications =  service.getAllCertification();
		model.addAttribute("certifications",Certifications);
		
	    return "admin/certifications";
	}
	@GetMapping("/addCertification")
	public String AddCertification(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
	    return "admin/addcertification";
	}
	
	@PostMapping("/addCertification")
	public String ClasseRegisterSuccess(@ModelAttribute("Certification") String certification,Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Certification cert = new Certification();
		cert.setCertificationName(certification);
		service.createCertification(cert);
        return "redirect:/admin/certifications";
    }
	@GetMapping("/delCert/{id}")
	public String DelCert(@PathVariable("id") Long id, Model model) {
		service.deleteCertification(id);
		return "redirect:/admin/certifications";
	}
	
	/*********************Requests**********************/

	@GetMapping("/requests")
	public String AllRequests(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Request> Requests =  service.getAllRequests();
		model.addAttribute("requests",Requests);
	    return "admin/requests";
	}
	@GetMapping("/checkRequest/{id}")
	public String checkRequest(@PathVariable("id") Long id, Model model,RedirectAttributes redirAttrs) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Request req = service.getRequestById(id);
		req.setChecked(1);
		reqRepo.save(req);
		redirAttrs.addFlashAttribute("success", "Request Checked !");
	    return "redirect:/admin/requests";
	}
	
	@GetMapping("/delRequest/{id}")
	public String DelRequest(@PathVariable("id") Long id, Model model,RedirectAttributes redirAttrs) {
		service.deleteRequest(id);
		redirAttrs.addFlashAttribute("success", "Request deleted !");
	    return "redirect:/admin/requests";
	}
	
	
	//////////Documents///////////////
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
		model.addAttribute("docs",docrepo.findAll());
	    return "admin/listSharedDocs";
	}
	
	/*******************Announce*********************/
	
	@GetMapping("/listannounce")
	public String listAnnounce(Model model) {
		UserEntity user =  userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		List<Announce> ans = service.getAllAnnounce();
		model.addAttribute("ans",ans);
		
	    return "admin/listannounce";
	}
	@GetMapping("/addannounce")
	public String addAnnounce(Model model) {
		UserEntity user = userRepo.findByEmail(getUserUsername());
		model.addAttribute("user",user);
		Announce announce = new Announce();
		model.addAttribute("announce",announce);
	    return "admin/addannounce";
	}
	@PostMapping("/addannounce")
	public String AnnounceregisterSuccess(@ModelAttribute("an") Announce an,RedirectAttributes redirAttrs, Model model,@RequestParam("file")MultipartFile file ) {
		String FileName = org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename());
    	if(FileName.contains("..")) {
    		System.out.println("not a proper file ");
    	}
    	try {
    		an.setImageU(Base64.getEncoder().encodeToString(file.getBytes()));
			System.out.println("cv");
		} catch (IOException e) {
			System.out.println("dowiw");
			e.printStackTrace();
		}
    	
        	service.createAnnounce(an);
         //  redirAttrs.addFlashAttribute("success", "Account created! Check your mail to activate Your Account");
            return "redirect:/admin/listannounce";
		
	}
	@GetMapping("/delAnnounce/{id}")
	public String DelAnnounce(@PathVariable("id") Long id, Model model,RedirectAttributes redirAttrs) {
		service.deleteAnnounce(id);
		redirAttrs.addFlashAttribute("success", "listannounce deleted !");
	    return "redirect:/admin/listannounce";
	}
	//////////////////////////////////////////
	@GetMapping("/inbox")
	public String inbox(Model model) {
 
	    return "student/mail/inbox";
	}
	@GetMapping("/composeMail")
	public String composeMail(Model model) {
 
	    return "student/mail/compose";
	}
	
	
}
