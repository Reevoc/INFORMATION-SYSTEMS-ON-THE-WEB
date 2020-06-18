package it.uniroma3.siw.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.validation.CredentialsValidator;
import it.uniroma3.siw.taskmanager.controller.validation.UserValidator;
import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;

@Controller
public class AuthenticationController {
	
	@Autowired
	CredentialsService credentialService;
	
	@Autowired
	UserValidator userValidator;
	
	@Autowired
	CredentialsValidator credentialsValidator;
	
	@RequestMapping(value = {"/users/register"}, method = RequestMethod.GET)
	public String showRegisterForm(Model model) {
		
		model.addAttribute("userForm", new User());
		model.addAttribute("credentialsForm", new Credentials());
		return "registerUser";
	}
	
	@RequestMapping(value = {"/users/register"}, method = RequestMethod.POST)
	public String registerUser(	@Validated @ModelAttribute("userForm") User user,
								BindingResult userBindingResult,
								@Validated @ModelAttribute("credentialsForm") Credentials credentials,
								BindingResult credentialsBindingResult,
								Model model) {
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);
		
		if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
			credentials.setUser(user);
			credentialService.saveCredentials(credentials);
			return "registrationSuccessful";
		}
		return "registerUser";
	}
}
