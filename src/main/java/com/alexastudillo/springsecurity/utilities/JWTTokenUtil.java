package com.alexastudillo.springsecurity.utilities;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.alexastudillo.springsecurity.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;

@Component
public class JWTTokenUtil {
	private final String jwtSecretKey = "secretkey";
	private final String jwtIssuer = "alexastudillo.com";
	private final String authoritiesKey = "authoritiesInformation";
	private final long jwtExpirationTime = 86_400_000; // 1 day

	private final Logger logger;

	public JWTTokenUtil(final Logger logger) {
		this.logger = logger;
	}

	public String generateToken(final User user) {
		final String authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		return JWT.create().withSubject(String.format("%s,%s", user.getId(), user.getUsername()))
				.withClaim(authoritiesKey, authorities).withIssuer(jwtIssuer).withIssuedAt(new Date())
				.withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
				.sign(Algorithm.HMAC512(jwtSecretKey));
	}

	public Collection<GrantedAuthority> getAuthorities(final String token) {
		final Claim claim = JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(token).getClaim(authoritiesKey);
		return Arrays.stream(claim.asString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	public String getUserId(final String token) {
		return JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(token).getSubject().split(",")[0];
	}

	public String getUsername(final String token) {
		return JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(token).getSubject().split(",")[1];
	}

	public Date getExpirationDate(final String token) throws TokenExpiredException {
		return JWT.require(Algorithm.HMAC512(jwtSecretKey)).build().verify(token).getExpiresAt();
	}

	public boolean validate(final String token) {
		try {
			JWT.require(Algorithm.HMAC512(jwtSecretKey.getBytes())).build().verify(token);
			return true;
		} catch (JWTVerificationException e) {
			logger.error("JWT fail verification - {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Ilegal argument - {}", e.getMessage());
		}
		return false;
	}
}
