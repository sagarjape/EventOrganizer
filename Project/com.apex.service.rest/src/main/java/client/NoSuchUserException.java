package client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "No such User")

public class NoSuchUserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6087717935038970528L;
}
