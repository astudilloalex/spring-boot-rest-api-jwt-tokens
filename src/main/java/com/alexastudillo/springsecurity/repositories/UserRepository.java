package com.alexastudillo.springsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alexastudillo.springsecurity.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findByUsername(final String username);
}
