package it.uniroma3.siw.taskmanager.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Task;
import it.uniroma3.siw.taskmanager.repository.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Transactional
	public Task getTask(Long id) {
		Optional<Task> result = this.taskRepository.findById(id);
		return result.orElse(null);
	}
	
	@Transactional
	public void deleteTask(Task task) {
		this.taskRepository.delete(task);
	}
	
	@Transactional
	public Task saveTask(Task task) {
		return this.taskRepository.save(task);
	}
	
	@Transactional
	public Task setCompleted(Task task) {
		task.setCompleted(true);
		return this.taskRepository.save(task);
	}
	
	@Transactional 
	public void deleteById(Long id) {
		this.taskRepository.deleteById(id);
	}
}
