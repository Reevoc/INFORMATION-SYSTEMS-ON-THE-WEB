package it.uniroma3.siw.taskmanager.controller.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.taskmanager.model.Comment;

@Component
public class CommentValidator implements Validator {

	
	@Override
	public boolean supports(Class<?> clazz) {
	
		return false;
	}

	@Override
	public void validate(Object target, Errors errors) {
		Comment comment = (Comment) target;
		String description = comment.getDescription().trim();
		
		if (description.isEmpty()) {
			errors.rejectValue("description", "required");
		}
	}

}
