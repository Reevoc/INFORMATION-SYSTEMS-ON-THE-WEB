package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.taskmanager.model.Comment;
import it.uniroma3.siw.taskmanager.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired 
	CommentRepository commentRepository;

	public List<Comment> getAllComments(){
		List<Comment> result = new ArrayList<>();
		Iterable<Comment> iterable = this.commentRepository.findAll();
		for(Comment cred : iterable)
			result.add(cred);
		return result;
	}

}

