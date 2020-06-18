package it.uniroma3.siw.taskmanager.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

@Entity
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(updatable = false, nullable = false)
	private LocalDateTime startTimestamp;
	
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER) //anche se e' gia' eager per default
	private User owner;
	
	@ManyToMany
	private List<User> members;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id")
	private List<Task> tasks;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "project_id")
	private List<Tag> tags;
	
	public Project() {
		this.members = new ArrayList<User>();
		this.tasks = new ArrayList<Task>();
		this.tags = new ArrayList<Tag>();
	}

	public Project(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	@PrePersist
	protected void onPersist() {
		this.startTimestamp = LocalDateTime.now();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<User> getMembers() {
		return members;
	}

	public void setMembers(List<User> members) {
		this.members = members;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}
	
	public void addTag(Tag tag) {
		this.tags.add(tag);
	}
	
	
	
	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	public void addMember(User member) {
		if(!this.members.contains(member)) {
			this.members.add(member);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Long getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", startTimestamp=" + startTimestamp + ", description="
				+ description + "]";
	}
	
	
	
	
}
