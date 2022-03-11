package com.alexastudillo.springsecurity.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alexastudillo.springsecurity.entities.User;
import com.alexastudillo.springsecurity.handlers.ResponseHandler;
import com.alexastudillo.springsecurity.utilities.JWTTokenUtil;
import com.alexastudillo.springsecurity.utilities.SecurityConstants;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final JWTTokenUtil jwtTokenUtil;

	public JWTAuthenticationFilter(final AuthenticationManager authenticationManager, final JWTTokenUtil jwtTokenUtil) {
		super(authenticationManager);
		this.jwtTokenUtil = jwtTokenUtil;
		setFilterProcessesUrl(SecurityConstants.SIGN_IN_URL);
	}

	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException {
		try {
			User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
			return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),
					user.getPassword(), user.getAuthorities()));
		} catch (StreamReadException e) {
			throw new RuntimeException(e);
		} catch (DatabindException e) {
			responseClient(response,
					new ResponseHandler().generateResponseWithoutData(e.getMessage(), HttpStatus.BAD_REQUEST));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
		final String token = jwtTokenUtil.generateToken((User) authResult.getPrincipal());
		final Map<String, String> mapRes = new HashMap<String, String>();
		mapRes.put("token", token);
		response.setContentType("application/json");
		response.getWriter().write(new ObjectMapper().writeValueAsString(
				new ResponseHandler().generateResponse("successful", HttpStatus.OK, mapRes).getBody()));
		response.getWriter().flush();
	}

	@Override
	protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException failed) throws IOException, ServletException {
		final HttpStatus status = HttpStatus.UNAUTHORIZED;
		if (failed.getMessage().equals("Bad credentials")) {
			response.setStatus(status.value());
		}
		response.setContentType("application/json");
		response.getWriter().write(new ObjectMapper().writeValueAsString(
				new ResponseHandler().generateResponseWithoutData(failed.getMessage(), status).getBody()));
		response.getWriter().flush();
	}

	private void responseClient(final HttpServletResponse response, final ResponseEntity<Object> responseEntity) {
		try {
			response.setStatus(responseEntity.getStatusCodeValue());
			response.setContentType("application/json");
			response.getWriter().write(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
			response.getWriter().flush();
		} catch (IOException e) {
		}
	}
}
