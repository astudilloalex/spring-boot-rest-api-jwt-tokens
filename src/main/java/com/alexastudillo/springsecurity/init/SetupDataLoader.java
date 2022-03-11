package com.alexastudillo.springsecurity.init;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alexastudillo.springsecurity.entities.Privilege;
import com.alexastudillo.springsecurity.entities.Role;
import com.alexastudillo.springsecurity.entities.User;
import com.alexastudillo.springsecurity.repositories.PrivilegeRepository;
import com.alexastudillo.springsecurity.repositories.RoleRepository;
import com.alexastudillo.springsecurity.repositories.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	private boolean alreadySetup = false;

	private final PrivilegeRepository privilegeRepository;
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SetupDataLoader(final PrivilegeRepository privilegeRepository, final RoleRepository roleRepository,
			final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
		this.privilegeRepository = privilegeRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (alreadySetup)
			return;
		final Privilege readPrivilege = createPrivilege("READ");
		final Set<Privilege> privileges = new HashSet<Privilege>();
		privileges.add(createPrivilege("CREATE"));
		privileges.add(readPrivilege);
		privileges.add(createPrivilege("UPDATE"));
		privileges.add(createPrivilege("DELETE"));
		final Role adminRole = createRole("ROLE_ADMIN", privileges);
		final Set<Role> roles = new HashSet<Role>();
		roles.add(adminRole);
		roles.add(createRole("ROLE_USER", new HashSet<Privilege>(Arrays.asList(readPrivilege))));
		User user = userRepository.findByUsername("superuser");
		if (user == null) {
			user = new User();
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setCredentialsNonExpired(true);
			user.setEnabled(true);
			user.setPassword(passwordEncoder.encode("alexastudillo"));
			user.setRoles(new HashSet<Role>(Arrays.asList(adminRole)));
			user.setUsername("superuser");
			userRepository.save(user);
		}
		alreadySetup = true;
	}

	@Transactional
	private Privilege createPrivilege(final String name) {
		Privilege privilege = privilegeRepository.findByName(name);
		if (privilege == null) {
			privilege = new Privilege(name, true);
			privilegeRepository.save(privilege);
		}
		return privilege;
	}

	@Transactional
	private Role createRole(final String name, final Set<Privilege> privileges) {
		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = new Role(name, true, privileges);
			roleRepository.save(role);
		}
		return role;
	}
}
