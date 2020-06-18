package it.uniroma3.siw.taskmanager.controller;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The UserController handles all interactions involving User data.
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserValidator userValidator;

    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    CredentialsService credentialsService;
    
    @Autowired
    CredentialsValidator credentialsValidator;
    
    @Autowired
    SessionData sessionData;

    
    @RequestMapping(value = { "/home" }, method = RequestMethod.GET)
    public String home(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "home";
    }

   
    @RequestMapping(value = { "/users/me" }, method = RequestMethod.GET)
    public String me(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        Credentials credentials = sessionData.getLoggedCredentials();
        System.out.println(credentials.getPassword());
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("credentials", credentials);

        return "userProfile";
    }

    
    @RequestMapping(value = { "/admin" }, method = RequestMethod.GET)
    public String admin(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        model.addAttribute("user", loggedUser);
        return "admin";
    }

    @RequestMapping(value = { "/admin/users" }, method = RequestMethod.GET)
    public String usersList(Model model) {
        User loggedUser = sessionData.getLoggedUser();
        List<Credentials> allCredentials = this.credentialsService.getAllCredentials();
        
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("credentialsList", allCredentials);
        return "allUsers";
    }
    
    @RequestMapping(value = { "/admin/users/{username}/delete" }, method = RequestMethod.POST)
    public String removeUser(Model model, @PathVariable String username) {
        this.credentialsService.deleteCredentials(username);
        return "redirect:/admin/users";
    }
    
    @RequestMapping(value = {"/users/me/update"}, method = RequestMethod.GET)
    public String updateUserForm(Model model) {
    	 User loggedUser = sessionData.getLoggedUser();
    	 Credentials loggedCredentials = sessionData.getLoggedCredentials();
    	 model.addAttribute("loggedUser", loggedUser);
    	 model.addAttribute("userForm", new User());
    	 model.addAttribute("loggedCredentials", loggedCredentials);
    	 model.addAttribute("credentialsForm", new Credentials());
    	 return "updateUser";
    }
    
	@RequestMapping(value = {"/users/me/update"}, method = RequestMethod.POST)
	public String updateUser(	@Validated @ModelAttribute("userForm") User user,
								BindingResult userBindingResult,
								@Validated @ModelAttribute("credentialsForm") Credentials credentials,
								BindingResult credentialsBindingResult,
								Model model) {
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);
		Credentials loggedCredentials = sessionData.getLoggedCredentials();
		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			this.credentialsService.updateCredentials(credentials, user, loggedCredentials.getUsername());
			this.sessionData.updateWithUpdatedUsername(credentials.getUsername());
			return "changeSuccessful";
		}
		User loggedUser = loggedCredentials.getUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("loggedCredentials", loggedCredentials);
		return "updateUser";
	}
}
