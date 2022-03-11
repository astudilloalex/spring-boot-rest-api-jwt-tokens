package com.alexastudillo.springsecurity.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.alexastudillo.springsecurity.filters.JWTAuthenticationFilter;
import com.alexastudillo.springsecurity.filters.JWTAuthorizationFilter;
import com.alexastudillo.springsecurity.services.UserService;
import com.alexastudillo.springsecurity.utilities.JWTTokenUtil;
import com.alexastudillo.springsecurity.utilities.SecurityConstants;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserService userService;
	private final JWTTokenUtil jwtTokenUtil;

	public WebSecurityConfig(final UserService userService, final JWTTokenUtil jwtTokenUtil) {
		this.userService = userService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		final JWTAuthenticationFilter authFilter = new JWTAuthenticationFilter(authenticationManager(), jwtTokenUtil);
		final JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(authenticationManager(),
				jwtTokenUtil);
		http.cors().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_IN_URL).permitAll().anyRequest().authenticated()
				.and().addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class).sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
