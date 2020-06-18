package it.uniroma3.siw.taskmanager.controller;

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
import it.uniroma3.siw.taskmanager.controller.validation.CommentValidator;
import it.uniroma3.siw.taskmanager.controller.validation.ProjectValidator;
import it.uniroma3.siw.taskmanager.controller.validation.TaskValidator;
import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.service.ProjectService;
import it.uniroma3.siw.taskmanager.service.TaskService;
import it.uniroma3.siw.taskmanager.service.UserService;

@Controller
public class CommentController {

	@Autowired
	ProjectService projectService;

	@Autowired
	UserService userService;

	@Autowired
	ProjectValidator projectValidator;

	@Autowired
	CommentValidator commentValidator;

	@Autowired
	SessionData sessionData;

	@Autowired
	TaskService taskService;

	@Autowired
	TaskValidator taskValidator;

	@RequestMapping(value = {"/{taskId}/comment/add"}, method = RequestMethod.POST)
	public String addComment(@Validated @ModelAttribute("comment") Comment comment,
			BindingResult projectBindingResult,
			@PathVariable Long taskId,
			Model model ){
		Task task = this.taskService.getTask(taskId);
		task.addComment(comment);
		commentValidator.validate(comment, projectBindingResult);
		if (!projectBindingResult.hasErrors()) {
			this.taskService.saveTask(task);
			return "redirect:/task/" + task.getId();
		}
		return "addTask";

	}
}
