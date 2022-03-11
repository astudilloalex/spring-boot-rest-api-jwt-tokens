package com.alexastudillo.springsecurity.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexastudillo.springsecurity.entities.Privilege;
import com.alexastudillo.springsecurity.entities.Role;
import com.alexastudillo.springsecurity.entities.User;
import com.alexastudillo.springsecurity.repositories.PrivilegeRepository;
import com.alexastudillo.springsecurity.repositories.RoleRepository;
import com.alexastudillo.springsecurity.repositories.UserRepository;

@Service
@Transactional
public class UserService implements UserDetailsService {
	private final PrivilegeRepository privilegeRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public UserService(final PrivilegeRepository privilegeRepository, final RoleRepository roleRepository,
			final UserRepository userRepository) {
		this.privilegeRepository = privilegeRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		final User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("username-does-not-exist");
		}
		final Set<Role> roles = new HashSet<Role>();
		for (final Role role : roleRepository.findByUserId(user.getId())) {
			role.setPrivileges(new HashSet<Privilege>(privilegeRepository.findByRoleId(role.getId())));
			roles.add(role);
		}
		user.setRoles(roles);
		return user;
	}
}
