package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;

@Service
public class CredentialsService {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredentialsRepository credentialsRepository;

	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> credenziali = this.credentialsRepository.findByUsername(username);
		return credenziali.orElse(null);
	}

	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	@Transactional
	public List<Credentials> getAllCredentials() {
		List<Credentials> result = new ArrayList<>();
		Iterable<Credentials> iterable = this.credentialsRepository.findAll();
		for(Credentials cred : iterable)
			result.add(cred);
		return result;
	}

	public Credentials findByUsername(String username) {
		return this.credentialsRepository.findByUsername(username).get();
	}

	public void deleteCredentials(String username) {
		this.credentialsRepository.deleteCredentialsByUsername(username);
	}



	@Transactional
	public void updateCredentials(Credentials updatedCredentials, User updatedUser, String originalUsername) {
		Credentials original = this.findByUsername(originalUsername);
		original.setUsername(updatedCredentials.getUsername());
		original.setPassword(updatedCredentials.getPassword());
		original.getUser().setFirstName(updatedUser.getFirstName());
		original.getUser().setLastName(updatedUser.getLastName());
		this.saveCredentials(original);
	}

}
