package ar.com.rescuesync.rest.api.client

import org.bonitasoft.web.extension.rest.RestAPIContext
import ar.com.rescuesync.rest.api.util.ProcessParameterUtils

/**
 * Implementa el patrón Factory para centralizar la creación y configuración
 * de instancias de {@code ApiNacionalClient}, obteniendo los parámetros
 * necesarios desde el contexto de la extensión REST.
 */
final class ApiNacionalClientFactory {

    static ApiNacionalClient create(RestAPIContext context) {

		Map<String, String> parameters =
			ProcessParameterUtils.getRequiredParameters(
				context,
				"api_nacional_url",
				"api_nacional_path",
				"api_nacional_user",
				"api_nacional_password"
			)

		String baseUrl = joinUrl(
			parameters["api_nacional_url"],
			parameters["api_nacional_path"]
		)

        return new ApiNacionalClient(baseUrl, parameters["api_nacional_user"], parameters["api_nacional_password"])
    }

    private static String joinUrl(String url, String path) {
        return url.replaceAll('/+$', '') +
            '/' +
            path.replaceAll('^/+', '')
    }

    private ApiNacionalClientFactory() {
        // Utility class
    }
}