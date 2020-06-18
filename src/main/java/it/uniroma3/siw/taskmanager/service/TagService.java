package it.uniroma3.siw.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Tag;
import it.uniroma3.siw.taskmanager.repository.TagRepository;

@Service
public class TagService {

	@Autowired
	TagRepository tagRepository;
	
	@Transactional
	public Tag getTag(Long id) {
		return this.tagRepository.findById(id).get();
	}

	@Transactional
	public void saveTag(Tag tagToUpdate) {
		this.tagRepository.save(tagToUpdate);
	}

	@Transactional
	public void deleteById(Long tagId) {
		this.tagRepository.deleteById(tagId);		
	}
}
