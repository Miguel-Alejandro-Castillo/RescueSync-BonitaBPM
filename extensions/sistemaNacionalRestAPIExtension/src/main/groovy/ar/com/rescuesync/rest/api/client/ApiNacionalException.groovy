package ar.com.rescuesync.rest.api.client

final class ApiNacionalException extends RuntimeException {

	final int statusCode
	final String responseBody

	ApiNacionalException(
		int statusCode,
		String message,
		String responseBody
	) {
		super(message)

		this.statusCode = statusCode
		this.responseBody = responseBody
	}

	ApiNacionalException(
		int statusCode,
		String message,
		String responseBody,
		Throwable cause
	) {
		super(message, cause)

		this.statusCode = statusCode
		this.responseBody = responseBody
	}
}