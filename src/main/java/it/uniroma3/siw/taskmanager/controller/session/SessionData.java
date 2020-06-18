package it.uniroma3.siw.taskmanager.controller.session;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;



import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;



@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData {



	private User user;
	private Credentials credentials;



	@Autowired
	private CredentialsRepository credentialsRepository;



	public Credentials getLoggedCredentials() {
		if(this.credentials == null) {
			this.update();
		}
		return this.credentials;
	}



	public User getLoggedUser() {
		if(this.user == null) {
			this.update();
		}
		return this.user;
	}



	public void update() {
		Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		//UserDetails loggedUserDetails = (UserDetails) obj;



		if(obj instanceof UserDetails) { // login normale
			UserDetails userDetails = (UserDetails)obj;
			this.credentials = this.credentialsRepository.findByUsername(userDetails.getUsername()).get();
			this.user = this.credentials.getUser();
		} else if(obj instanceof DefaultOidcUser) { //oauth
			DefaultOidcUser oidcUser = (DefaultOidcUser)obj;
			//controllo se dentro il mio db esiste un utente con questo username
			if (this.credentialsRepository.findByUsername(oidcUser.getFullName()).isPresent()) {// login oauth google
				this.credentials = this.credentialsRepository.findByUsername(oidcUser.getFullName()).get();
				this.user = this.credentials.getUser();
			}else{
				Credentials credentials = new Credentials();//creo nuove credenziali
				credentials.setUsername(oidcUser.getFullName());//passo email oauth2
				credentials.setPassword("[PROTECTED]");//setto la password
				credentials.setRole(Credentials.DEFAULT_ROLE);//setto il ruolo
				credentials.setUser(new User(oidcUser.getGivenName(),oidcUser.getFamilyName()));//imposto l'utente
				this.credentialsRepository.save(credentials);//salvo le credenziali
				//faccio la stessa cosa ora del log in ma ho registrato anche le cred e lo user
				this.credentials = this.credentialsRepository.findByUsername(oidcUser.getFullName()).get();
				this.user = this.credentials.getUser();
			}
		}
	}



	public void updateWithUpdatedUsername(String updatedUsername) {
		this.credentials = this.credentialsRepository.findByUsername(updatedUsername).get();
		this.credentials.setPassword("[PROTECTED]"); 
		this.user = this.credentials.getUser();
	}



}
