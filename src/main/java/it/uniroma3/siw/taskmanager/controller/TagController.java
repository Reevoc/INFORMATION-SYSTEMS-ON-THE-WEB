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
import it.uniroma3.siw.taskmanager.controller.validation.TagValidator;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TagService;
import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class TagController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	SessionData sessionData;

	@Autowired
	TaskService taskService;

	@Autowired
	TaskValidator taskValidator;

	//@Autowired 
	//CommentService commentService;

	@Autowired
	TagValidator tagValidator;

	@Autowired
	TagService tagService;

	@RequestMapping (value = {"/{projectId}/tag/add"}, method = RequestMethod.GET)
	public String addTagView( @PathVariable Long projectId, Model model) {

		Project project = this.projectService.getProjectById(projectId);

		if(!project.getMembers().contains(sessionData.getLoggedUser())) {
			return "No Authorization for this user";
		}

		if (project.equals(null)) {
			return "project";
		}

		model.addAttribute("project", project);
		model.addAttribute("tag",new Tag());
		return "addTag";
	}

	@RequestMapping (value = {"/{projectId}/tag/add"}, method = RequestMethod.POST)
	public String addTag(@Validated @ModelAttribute("tag") Tag tag,
			BindingResult projectBindingResult,
			@PathVariable Long projectId,
			Model model ) {
		Project project = projectService.getProjectById(projectId);
		project.addTag(tag);
		tagValidator.validate(tag, projectBindingResult);

		if (!projectBindingResult.hasErrors()) {
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		
		return "addTag";
	}
	@RequestMapping(value = {"/tag/{tagId}"}, method = RequestMethod.GET)
	public String viewTag(@PathVariable Long tagId,Model model) {
		Tag tag = this.tagService.getTag(tagId);
		model.addAttribute("tag", tag);
		model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
		return "tag";
	}
	
	@RequestMapping(value = {"/projects/{projectId}/task/{taskId}"}, method = RequestMethod.GET)
	public String addTagToTask(@PathVariable Long projectId, @PathVariable Long taskId,Model model ){
		Project project = this.projectService.getProjectById(projectId);
		Task task = this.taskService.getTask(taskId);
		List <Tag> tagList = project.getTags(); 
		model.addAttribute("tagList", tagList);
		model.addAttribute("task", task);
		return "addTagToTask" ;
	}
	
	@RequestMapping (value = {"/{taskId}/{tagId}/addToTask"}, method = RequestMethod.POST)
	public String addTagToTask(@PathVariable Long taskId, @PathVariable Long tagId) {
		Task task = this.taskService.getTask(taskId);
		task.addTags(this.tagService.getTag(tagId));
		this.taskService.saveTask(task);
		return "redirect:/task/" + task.getId();
	}
	
	@RequestMapping (value = {"/tag/{tagId}/edit"}, method = RequestMethod.GET)
	public String editTagForm(Model model,
								@PathVariable Long tagId) {
		Tag tag = this.tagService.getTag(tagId);
		model.addAttribute("tag", tag);
		model.addAttribute("tagForm", new Tag());
		return "editTag";
	}
	
	@RequestMapping (value = {"/tag/{tagId}/edit"}, method = RequestMethod.POST)
	public String editTag(Model model,
								@PathVariable Long tagId,
								@Validated @ModelAttribute("tagForm") Tag tagForm,
								BindingResult tagFormBindingResult) {
		tagValidator.validate(tagForm, tagFormBindingResult);
		Tag tagToUpdate = this.tagService.getTag(tagId);
		if(tagForm != null && !tagFormBindingResult.hasErrors()) {
			tagToUpdate.setName(tagForm.getName());
			tagToUpdate.setDescription(tagForm.getDescription());
			tagToUpdate.setColor(tagForm.getColor());
			this.tagService.saveTag(tagToUpdate);
			return "editTagSuccessful";
		}
		model.addAttribute("tag", tagToUpdate);
		model.addAttribute("tagForm", tagForm);
		return "editTag";
	}
	
	@RequestMapping(value = {"/tag/{projectId}/{tagId}/delete"}, method = RequestMethod.POST)
	public String deleteTag(@PathVariable Long tagId,
							@PathVariable Long projectId) {
		Tag tag = this.tagService.getTag(tagId);
		Project project = this.projectService.getProjectById(projectId);
		project.getTags().remove(tag);
		this.tagService.deleteById(tagId);
		return "redirect:/projects/" + projectId;//qua
	}
}
