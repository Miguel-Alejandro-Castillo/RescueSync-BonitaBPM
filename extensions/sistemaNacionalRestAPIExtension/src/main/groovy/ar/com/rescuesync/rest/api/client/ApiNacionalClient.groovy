package ar.com.rescuesync.rest.api.client

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

final class ApiNacionalClient {

	private static final Duration CONNECT_TIMEOUT =
		Duration.ofSeconds(10)

	private static final Duration REQUEST_TIMEOUT =
		Duration.ofSeconds(30)

	private final String baseUrl
	private final String username
	private final String password

	private final HttpClient httpClient

	private String accessToken
	private String tokenType = "Bearer"

	ApiNacionalClient(
		String baseUrl,
		String username,
		String password
	) {
		this.baseUrl = requireValue(
			baseUrl,
			"baseUrl"
		).replaceAll('/+$', '')

		this.username = requireValue(
			username,
			"username"
		)

		this.password = requireValue(
			password,
			"password"
		)

		this.httpClient = HttpClient.newBuilder()
			.connectTimeout(CONNECT_TIMEOUT)
			.build()
	}

	Object get(String path) {
		return executeAuthenticatedRequest(
			"GET",
			path,
			null,
			false
		)
	}

	Object post(
		String path,
		Object body
	) {
		return executeAuthenticatedRequest(
			"POST",
			path,
			body,
			false
		)
	}

	Object put(
		String path,
		Object body
	) {
		return executeAuthenticatedRequest(
			"PUT",
			path,
			body,
			false
		)
	}

	Object delete(String path) {
		return executeAuthenticatedRequest(
			"DELETE",
			path,
			null,
			false
		)
	}

	/**
	 * Ejecuta una petición autenticada.
	 *
	 * Si la API Nacional responde 401, se invalida el token,
	 * se vuelve a realizar login y se reintenta una única vez.
	 */
	private Object executeAuthenticatedRequest(
		String method,
		String path,
		Object body,
		boolean retried
	) {
		ensureAuthenticated()

		HttpResponse<String> response =
			sendRequest(
				method,
				path,
				body
			)

		if (response.statusCode() == 401 && !retried) {

			invalidateToken()

			ensureAuthenticated()

			response = sendRequest(
				method,
				path,
				body
			)
		}

		return handleResponse(response)
	}

	/**
	 * Realiza login únicamente cuando no existe un token.
	 */
	private synchronized void ensureAuthenticated() {
		if (accessToken != null &&
			!accessToken.trim().isEmpty()) {

			return
		}

		login()
	}

	/**
	 * Autentica el cliente contra /auth/login.
	 */
	private void login() {

		Map<String, String> credentials = [
			username: username,
			password: password
		]

		HttpRequest request =
			HttpRequest.newBuilder()
				.uri(buildUri("/auth/login"))
				.timeout(REQUEST_TIMEOUT)
				.header(
					"Accept",
					"application/json"
				)
				.header(
					"Content-Type",
					"application/json"
				)
				.POST(
					HttpRequest.BodyPublishers.ofString(
						JsonOutput.toJson(credentials)
					)
				)
				.build()

		HttpResponse<String> response =
			send(request)

		int statusCode =
			response.statusCode()

		if (statusCode < 200 ||
			statusCode >= 300) {

			throw new ApiNacionalException(
				statusCode,
				"No fue posible autenticar en la API Nacional",
				response.body()
			)
		}

		Object responseBody =
			parseResponseBody(response)

		updateAuthenticationData(
			statusCode,
			responseBody,
			response.body()
		)
	}

	/**
	 * Extrae:
	 *
	 * {
	 *   "data": {
	 *     "access_token": "...",
	 *     "token_type": "Bearer",
	 *     "expires_in": 3600
	 *   }
	 * }
	 *
	 * expires_in se ignora deliberadamente.
	 */
	private void updateAuthenticationData(
		int statusCode,
		Object responseBody,
		String rawResponseBody
	) {
		if (!(responseBody instanceof Map)) {
			throw invalidLoginResponse(
				statusCode,
				rawResponseBody
			)
		}

		Map responseMap =
			(Map) responseBody

		if (!(responseMap["data"] instanceof Map)) {
			throw invalidLoginResponse(
				statusCode,
				rawResponseBody
			)
		}

		Map data =
			(Map) responseMap["data"]

		String token =
			data["access_token"]
				?.toString()
				?.trim()

		String returnedTokenType =
			data["token_type"]
				?.toString()
				?.trim()

		if (!token) {
			throw new ApiNacionalException(
				statusCode,
				"La API Nacional no devolvió 'data.access_token'",
				rawResponseBody
			)
		}

		this.accessToken = token

		this.tokenType =
			returnedTokenType ?: "Bearer"
	}

	/**
	 * Construye y ejecuta una petición autenticada.
	 */
	private HttpResponse<String> sendRequest(
		String method,
		String path,
		Object body
	) {
		HttpRequest.Builder builder =
			HttpRequest.newBuilder()
				.uri(buildUri(path))
				.timeout(REQUEST_TIMEOUT)
				.header(
					"Accept",
					"application/json"
				)
				.header(
					"Authorization",
					"${tokenType} ${accessToken}"
				)

		switch (method) {

			case "GET":
				builder.GET()
				break

			case "POST":
				builder
					.header(
						"Content-Type",
						"application/json"
					)
					.POST(
						createJsonBody(body)
					)
				break

			case "PUT":
				builder
					.header(
						"Content-Type",
						"application/json"
					)
					.PUT(
						createJsonBody(body)
					)
				break

			case "DELETE":
				builder.DELETE()
				break

			default:
				throw new IllegalArgumentException(
					"Método HTTP no soportado: ${method}"
				)
		}

		return send(
			builder.build()
		)
	}

	/**
	 * Ejecuta la petición HTTP.
	 */
	private HttpResponse<String> send(
		HttpRequest request
	) {
		try {

			return httpClient.send(
				request,
				HttpResponse.BodyHandlers.ofString()
			)

		} catch (InterruptedException exception) {

			Thread.currentThread().interrupt()

			throw new ApiNacionalException(
				503,
				"La comunicación con la API Nacional fue interrumpida",
				null,
				exception
			)

		} catch (IOException exception) {

			throw new ApiNacionalException(
				503,
				"No fue posible conectar con la API Nacional",
				null,
				exception
			)
		}
	}

	/**
	 * Valida el HTTP status y devuelve el body parseado.
	 */
	private Object handleResponse(
		HttpResponse<String> response
	) {
		int statusCode =
			response.statusCode()

		if (statusCode < 200 ||
			statusCode >= 300) {

			throw new ApiNacionalException(
				statusCode,
				"La API Nacional respondió HTTP ${statusCode}",
				response.body()
			)
		}

		return parseResponseBody(response)
	}

	/**
	 * Convierte el body JSON en un objeto Groovy.
	 */
	private Object parseResponseBody(
		HttpResponse<String> response
	) {
		String body =
			response.body()

		if (body == null ||
			body.trim().isEmpty()) {

			return null
		}

		try {

			return new JsonSlurper()
				.parseText(body)

		} catch (Exception exception) {

			throw new ApiNacionalException(
				response.statusCode(),
				"La API Nacional devolvió una respuesta JSON inválida",
				body,
				exception
			)
		}
	}

	/**
	 * Elimina la autenticación actual.
	 */
	private void invalidateToken() {
		this.accessToken = null
		this.tokenType = "Bearer"
	}

	private static ApiNacionalException invalidLoginResponse(
		int statusCode,
		String rawResponseBody
	) {
		return new ApiNacionalException(
			statusCode,
			"La respuesta de autenticación no contiene el objeto 'data'",
			rawResponseBody
		)
	}

	private static HttpRequest.BodyPublisher createJsonBody(
		Object body
	) {
		String json =
			JsonOutput.toJson(
				body != null
					? body
					: [:]
			)

		return HttpRequest.BodyPublishers
			.ofString(json)
	}

	private URI buildUri(
		String path
	) {
		String normalizedPath =
			path == null
				? ""
				: path.trim()

		normalizedPath =
			normalizedPath.replaceFirst(
				'^/+',
				''
			)

		return URI.create(
			"${baseUrl}/${normalizedPath}"
		)
	}

	private static String requireValue(
		String value,
		String fieldName
	) {
		if (value == null ||
			value.trim().isEmpty()) {

			throw new IllegalArgumentException(
				"El valor '${fieldName}' es obligatorio"
			)
		}

		return value.trim()
	}
}