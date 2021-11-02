package projet.rest.data.endpoints;


import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import projet.rest.data.models.ConfirmationToken;
import projet.rest.data.models.UserEntity;
import projet.rest.data.repositories.ConfirmationTokenRepository;
import projet.rest.data.repositories.UserRepository;
import projet.rest.data.services.SendEmailService;
import projet.rest.data.services.UserService;


@Controller
@Data
@AllArgsConstructor
public class Controlerr {
	@Autowired
    UserService service ;
	@Autowired
    SendEmailService SendEmailService;

	private UserRepository userrepo;
	private ConfirmationTokenRepository conftrepo;
	
	public String CheckRole () {
		Collection<? extends GrantedAuthority> authorities;
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    authorities = auth.getAuthorities();
	     
	    return authorities.toArray()[0].toString();
	}
	@GetMapping("/")
	public String returnindex(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
	    return "redirect:/Login";
	}
	@GetMapping("/Login")
	public String login(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
	    return "Other/login";
	}
	@GetMapping("/home")
	public String home(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/home";
	    }
	    return "Other/login";
	}
	@GetMapping("/logout-Success")
	public String logout() {
	    return "redirect:/Login";
	}
	@GetMapping("/default")
	public String defaulta(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/home";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/home";
	    }
	    
	    return "redirect:/admin/home";
	}
	@GetMapping("/my-account")
	public String myaccount(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/my-account";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/my-account";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/my-account";
	    }
	    return "Other/404";
	}
	@GetMapping("/my-profile")
	public String profile(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/my-profile";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/my-profile";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/my-profile";
	    }
	    return "Other/404";
	}
	@GetMapping("/composeMail")
	public String composeMail(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/composeMail";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/composeMail";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/composeMail";
	    }
	    return "Other/404";
	}
	@GetMapping("/inbox")
	public String inbox(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/inbox";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/inbox";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/inbox";
	    }
	    return "Other/404";
	}
	@GetMapping("/sent")
	public String sent(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/sent";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/sent";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/sent";
	    }
	    return "Other/404";
	}
	
	@GetMapping("/tables")
	public String tables(Model model) {
		if (CheckRole().equals("STUDENT")) {
	        return "redirect:/student/tables";
	    }
	    else if (CheckRole().equals("PROF")) {
	        return "redirect:/prof/tables";
	    }
	    else if (CheckRole().equals("ADMIN")) {
	        return "redirect:/admin/tables";
	    }
	    return "Other/404";
	}
	@GetMapping("/forgotpass")
	public String forgotpass(Model model) {
		
	    return "Other/forgot-password";
	}
	
	@PostMapping("/forgotpass")
	public String forgotpass1(@RequestParam("email") String email, RedirectAttributes redirAttrs) {
		UserEntity user = userrepo.findByEmail(email);
        if(user == null)
        {	
        redirAttrs.addFlashAttribute("error", "email doesn't exist");
        return "redirect:/forgotpass";
        }
        else
        {	//lazem na3mlou table o5ra mta3 tokens teb3a el password
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            conftrepo.save(confirmationToken);
            String text="To Change your password, please click here : "
                    +"http://localhost:9090/change-password/"+confirmationToken.getConfirmationToken();
            SendEmailService.changePassword(email,"Change Password !",text);
    		redirAttrs.addFlashAttribute("success", "Check Your Mail to Confirm new Password");

            return "redirect:/Login";
        }
	}
	@GetMapping("/change-password/{confirmationToken}")
	public String updPassword(Model model,@PathVariable String confirmationToken) {
		 ConfirmationToken token = conftrepo.findByConfirmationToken(confirmationToken);
		 if (token.getExpired()==1)
	        	return "redirect:/Login";
		 UserEntity user = userrepo.findByEmail(token.getUser().getEmail());
			model.addAttribute("user",user);
			model.addAttribute("token",token);
			return "Other/forgotpass_part2";
		
	}
	
	@PostMapping("/change-password/{confirmationToken}")
	public String updPassword1(@ModelAttribute("user") UserEntity user ,@PathVariable String confirmationToken, RedirectAttributes redirAttrs) {	
        ConfirmationToken token = conftrepo.findByConfirmationToken(confirmationToken);
        UserEntity olduser = userrepo.findByEmail(token.getUser().getEmail());

        olduser.setPassword(user.getPassword());
        System.out.println("Passworrrrd = "+olduser.getPassword());
        service.modifyUserEntity(olduser.getId(), olduser);
        token.setExpired(1);
		conftrepo.save(token);
		redirAttrs.addFlashAttribute("success", "Password Modified Successfully");

		return "redirect:/Login";
	}
	
	@GetMapping("/confirm-account/{email}")
	public String confirmUserAccount(Model model,@PathVariable String email) {
        UserEntity olduser = userrepo.findByEmail(email);
		 if (olduser.getVerified()==1)
	        	return "redirect:/Login";
			model.addAttribute("user",olduser);
			return "Other/UserVerification";
		
	}
	@PostMapping("/confirm-account/{email}")
    public String confirmUserAccount01(@ModelAttribute("user") UserEntity user ,@PathVariable String email, RedirectAttributes redirAttrs)
    {	
        UserEntity olduser = userrepo.findByEmail(email);
        olduser.setVerified(1);
        olduser.setPassword(user.getPassword());
        service.modifyUserEntity(olduser.getId(), olduser);
        userrepo.save(olduser);
		SendEmailService.welcomeMail(olduser.getEmail(),olduser.getFirstName());
		redirAttrs.addFlashAttribute("success", "Account Activated! Try to Login");
		return "redirect:/logout";

    }
	
}