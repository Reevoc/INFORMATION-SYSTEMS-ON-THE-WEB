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
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Project;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.model.User;
import it.uniroma3.siw.taskmanager.service.CommentService;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class TaskController {

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
	
	@Autowired 
	CommentService commentService;

	@RequestMapping(value = {"/task/{taskId}"}, method = RequestMethod.GET)
	public String taskView(Model model, @PathVariable Long taskId) {
		Task task = this.taskService.getTask(taskId);
		List <Comment> comments = this.commentService.getAllComments();
		User loggedUser = this.sessionData.getLoggedUser();
		model.addAttribute("comments",comments);
		model.addAttribute("task", task);
		model.addAttribute("comment", new Comment());
		model.addAttribute("loggedUser", loggedUser);
		return "task";
		
		}

	@RequestMapping (value = {"/{projectId}/task/add"}, method = RequestMethod.GET)
	public String addTaskView( @PathVariable Long projectId, Model model) {
		Project project = this.projectService.getProjectById(projectId);
		if(!project.getMembers().contains(sessionData.getLoggedUser())) {
			return "No Authorization for this user";
		}
		if (project.equals(null)) {
			return "project";
		}
		model.addAttribute("project", project);
		model.addAttribute("task",new Task());
		return "aggTask";
	}

	@RequestMapping (value = {"/{projectId}/task/add"}, method = RequestMethod.POST)
	public String addTask(@Validated @ModelAttribute("task") Task task,
			BindingResult projectBindingResult,
			@PathVariable Long projectId,
			Model model ) {
		Project project = projectService.getProjectById(projectId);
		project.getTasks().add(task);
		 taskValidator.validate(task, projectBindingResult);
		if (!projectBindingResult.hasErrors()) {
			this.projectService.saveProject(project);
			return "redirect:/projects/" + project.getId();
		}
		return "aggTask";
	}

	@RequestMapping(value = {"/task/{taskId}/edit"} , method = RequestMethod.GET)
	public String editTaskForm(@PathVariable Long taskId,Model model) {
		Task task = taskService.getTask(taskId);
		model.addAttribute("task",task);
		model.addAttribute("taskForm", new Task());
		return "editTask";
	}

	@RequestMapping(value = {"/task/{taskId}/edit"} , method = RequestMethod.POST)
	public String editTask(Model model,
							@PathVariable Long taskId,
							@ModelAttribute("taskForm") Task taskForm,
							BindingResult taskFormBindingResult) {
		this.taskValidator.validate(taskForm, taskFormBindingResult);
		Task taskToUpdate = this.taskService.getTask(taskId);
		if(!taskFormBindingResult.hasErrors() && taskForm!= null) {
			taskToUpdate.setName(taskForm.getName());
			taskToUpdate.setCompleted(taskForm.isCompleted());
			taskToUpdate.setDescription(taskForm.getDescription());
			this.taskService.saveTask(taskToUpdate);
		}
		return "redirect:/task/" + taskToUpdate.getId();
	}

	@RequestMapping(value = {"/task/{taskId}/delete"}, method = RequestMethod.POST)
	public String deleteTask(@PathVariable Long taskId) {
		this.taskService.getTask(taskId).getTags().clear();
		this.taskService.deleteById(taskId);
		return "redirect:/projects";
	}
}