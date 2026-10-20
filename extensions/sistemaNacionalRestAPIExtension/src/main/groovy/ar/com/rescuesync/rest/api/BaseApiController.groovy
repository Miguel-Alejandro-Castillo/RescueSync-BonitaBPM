package ar.com.rescuesync.rest.api

import ar.com.rescuesync.rest.api.client.ApiNacionalClient
import ar.com.rescuesync.rest.api.client.ApiNacionalClientFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiController
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder
import groovy.json.JsonOutput
import org.bonitasoft.web.extension.rest.RestApiResponse

/**
 * Controlador base para las extensiones de la API REST.
 *
 * Implementa el patrón Template Method: {@code doHandle} define el flujo
 * común de procesamiento, incluyendo la creación del cliente de la API
 * Nacional y la construcción de la respuesta HTTP, mientras que
 * {@code doAction} delega la lógica específica a las subclases.
 */
abstract class BaseApiController implements RestApiController {
	
		@Override
		final RestApiResponse doHandle(
			HttpServletRequest request,
			RestApiResponseBuilder responseBuilder,
			RestAPIContext context
		) {
			ApiNacionalClient client =
				ApiNacionalClientFactory.create(context)
	
			Object result = doAction(request, client)
	
			return jsonResponse(
				responseBuilder,
				HttpServletResponse.SC_OK,
				result
			)
		}
	
		protected abstract Object doAction(
			HttpServletRequest request,
			ApiNacionalClient client
		)
	
		private RestApiResponse jsonResponse(
			RestApiResponseBuilder responseBuilder,
			int status,
			Object body
		) {
			responseBuilder.withResponseStatus(status)
			responseBuilder.withResponse(JsonOutput.toJson(body))
	
			return responseBuilder.build()
		}
	}