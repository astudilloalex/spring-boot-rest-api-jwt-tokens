package com.alexastudillo.springsecurity.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "roles")
public class Role implements Serializable {
	private static final long serialVersionUID = 5747734568671958769L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Short id;

	@Column(name = "name", unique = true, nullable = false, length = 30)
	private String name;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Column(name = "creation_date", columnDefinition = "TIMESTAMP WITH TIME ZONE NOT NULL", updatable = false)
	@CreationTimestamp
	private Timestamp creationDate;

	@Column(name = "update_date", columnDefinition = "TIMESTAMP WITH TIME ZONE NOT NULL")
	@UpdateTimestamp
	private Timestamp updateDate;

	@ManyToMany
	@JoinTable(name = "role_privileges", joinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "privilege_id", referencedColumnName = "id") })
	@JsonIgnoreProperties("roles")
	private Set<Privilege> privileges;

	@ManyToMany(mappedBy = "roles")
	private Set<User> users;

	public Role() {
		// TODO Auto-generated constructor stub
	}

	public Role(final String name, final boolean active, final Set<Privilege> privileges) {
		this.name = name;
		this.active = active;
		this.privileges = privileges;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}
}
