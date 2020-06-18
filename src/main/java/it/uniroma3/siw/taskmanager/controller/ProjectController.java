package it.uniroma3.siw.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CredentialsService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class ProjectController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	CredentialsService credentialsService;

	@Autowired
	SessionData sessionData;

	@RequestMapping(value = {"/projects"}, method = RequestMethod.GET)
	public String myOwnedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectList = projectService.retrieveProjectsOwnedBy(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectList", projectList);
		return "myOwnedProjects";
	}

	@RequestMapping(value = {"/projects/sharedProjects"}, method = RequestMethod.GET)
	public String sharedProjects(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		List<Project> projectList = projectService.retrieveVisibleProjectsFor(loggedUser);
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectList", projectList);
		return "sharedProjects";
	}

	@RequestMapping(value = {"/projects/{projectId}"}, method = RequestMethod.GET)
	public String project(Model model,
			@PathVariable Long projectId) {
		Project project = projectService.getProjectById(projectId);
		if(project == null) {
			return "redirect:/projects";
		}

		List<User> members = userService.getMembers(project);
		User loggedUser = this.sessionData.getLoggedUser();

		if(!project.getOwner().equals(loggedUser) && !members.contains(loggedUser)) {
			return "redirect:/projects";
		}

		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("project", project);
		model.addAttribute("members", members);
		return "project";
	}

	@RequestMapping(value = {"/projects/add"}, method = RequestMethod.GET)
	public String createProjectForm(Model model) {
		User loggedUser = sessionData.getLoggedUser();
		model.addAttribute("loggedUser", loggedUser);
		model.addAttribute("projectForm", new Project());
		return "addProject";
	} 

	@RequestMapping(value = {"/projects/add"}, method = RequestMethod.POST)
	public String createProject(Model model,
			@Validated @ModelAttribute("projectForm") Project project,
			BindingResult projectBindingResult) {

		User loggedUser = sessionData.getLoggedUser();
		projectValidator.validate(project, projectBindingResult);
		if(!projectBindingResult.hasErrors()) {
			project.setOwner(loggedUser);
			project.addMember(loggedUser); //il proprietario è lui stesso un membro del progetto
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		model.addAttribute("loggedUser", loggedUser);
		return "addProject";
	} 

	@RequestMapping(value = {"/projects/{projectId}/update"}, method = RequestMethod.GET)
	public String updateProjectForm(	Model model,
			@PathVariable Long projectId) {
		Project project = projectService.getProjectById(projectId);
		model.addAttribute("project", project);
		model.addAttribute("projectId", projectId);
		model.addAttribute("projectForm", new Project());
		return "updateProject";
	} 

	@RequestMapping(value = {"/projects/{projectId}/update"}, method = RequestMethod.POST)
	public String updateProject(Model model,
			@ModelAttribute("projectId") Long projectId,
			BindingResult projectBindingResult,
			@Validated @ModelAttribute("projectForm") Project projectForm){

		projectValidator.validate(projectForm, projectBindingResult);
		Project project = this.projectService.getProjectById(projectId);
		if(!projectBindingResult.hasErrors()) {
			project.setName(projectForm.getName());
			project.setDescription(projectForm.getDescription());
			this.projectService.saveProject(project);
			return "redirect:/projects/" + projectId;
		}
		model.addAttribute("project", project);
		model.addAttribute("projectId", projectId);
		return "updateProject";
	}

	@RequestMapping (value = {"/projects/{project}/delete"}, method = RequestMethod.POST)
	public String removeProject(Model model, @PathVariable String project){
		this.projectService.deleteProjectByName(project);
		return "redirect:/projects";
	}

	@RequestMapping (value = { "/allusers/{projectId}" }, method = RequestMethod.GET)
	public String shareProjectForm(Model model,
			@PathVariable Long projectId) {
		List<User> allUsers = this.userService.getAllUsers();
		model.addAttribute("projectId", projectId);
		model.addAttribute("allUsers", allUsers);
		model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
		return "shareForAllUser";
	}

	@RequestMapping (value = { "/allusers/{projectId}/{userId}" }, method = RequestMethod.POST)
	public String shareProject(Model model,
			@PathVariable Long projectId,
			@PathVariable Long userId){
		Project project = this.projectService.getProjectById(projectId);
		this.projectService.shareProjectWithUser(project, this.userService.getUser(userId));
		return "sharedSuccessful";
	}
}