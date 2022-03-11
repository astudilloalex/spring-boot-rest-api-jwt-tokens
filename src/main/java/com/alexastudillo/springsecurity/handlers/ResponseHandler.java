package com.alexastudillo.springsecurity.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
	public ResponseEntity<Object> generateResponse(final String message, final HttpStatus status,
			final Object response) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", message);
		map.put("status", status.value());
		map.put("data", response);
		return new ResponseEntity<Object>(map, status);
	}

	public ResponseEntity<Object> generateResponse(final Map<String, Object> map, final HttpStatus status) {
		return new ResponseEntity<Object>(map, status);
	}

	public ResponseEntity<Object> generateResponseWithoutData(final String message, final HttpStatus status) {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", message);
		map.put("status", status.value());
		return new ResponseEntity<Object>(map, status);
	}
}
