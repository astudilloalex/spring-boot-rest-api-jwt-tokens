package com.alexastudillo.springsecurity.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexastudillo.springsecurity.entities.Role;
import com.alexastudillo.springsecurity.entities.User;
import com.alexastudillo.springsecurity.handlers.ResponseHandler;
import com.alexastudillo.springsecurity.repositories.RoleRepository;
import com.alexastudillo.springsecurity.repositories.UserRepository;

@RestController
public class UserController {
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public UserController(PasswordEncoder passwordEncoder, RoleRepository roleRepository,
			UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/api/v1/users/all")
	public ResponseEntity<Object> getAll() {
		final List<User> users = userRepository.findAll();
		return new ResponseHandler().generateResponse("successful", HttpStatus.OK, users);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping("/api/v1/add-user")
	public ResponseEntity<Object> createUser(@RequestBody final User user) {
		final User userSave = new User(user.getUsername(), passwordEncoder.encode(user.getPassword()));
		for (Role role : user.getRoles()) {
			userSave.addRole(roleRepository.findById(role.getId()).get());
		}
		if (userRepository.findByUsername(userSave.getUsername()) != null) {
			return new ResponseHandler().generateResponseWithoutData("username-already-exist", HttpStatus.BAD_REQUEST);
		}
		if (user.getUsername().length() < 4) {
			return new ResponseHandler().generateResponseWithoutData("username-min-length-4", HttpStatus.BAD_REQUEST);
		}
		if (user.getPassword().length() < 6) {
			return new ResponseHandler().generateResponseWithoutData("password-min-length-6", HttpStatus.BAD_REQUEST);
		}
		return new ResponseHandler().generateResponse("successful", HttpStatus.OK, userRepository.save(userSave));
	}
}
