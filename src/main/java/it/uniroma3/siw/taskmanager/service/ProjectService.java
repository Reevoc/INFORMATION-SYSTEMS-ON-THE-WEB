package it.uniroma3.siw.taskmanager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.repository.ProjectRepository;



@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	
	@Transactional
	public Project getProjectById(Long projectId) {
	Optional <Project> project = this.projectRepository.findById(projectId);
		return project.orElse(null);
	}
	
	@Transactional
	public void deleteProject(Project project) {
		this.projectRepository.delete(project);
	}
	
	@Transactional
	public Project saveProject(Project project) {
		return this.projectRepository.save(project);
	}
	
	@Transactional
	public Project shareProjectWithUser(Project project, User user) {
		project.addMember(user);
		return this.projectRepository.save(project);
	}

	@Transactional
	public List<Project> retrieveProjectsOwnedBy(User loggedUser) {
		return this.projectRepository.findByOwner(loggedUser);
	}

	public List<Project> retrieveVisibleProjectsFor(User loggedUser) {
		return this.projectRepository.findByMembers(loggedUser);
	}
	
	@Transactional
	public void deleteProjectByName(String name) {
		this.projectRepository.deleteByName(name);
	}
}
