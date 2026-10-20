package ar.com.rescuesync.rest.api.util

import org.bonitasoft.engine.api.ProcessAPI
import org.bonitasoft.engine.bpm.parameter.ParameterInstance
import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestAPIContext

final class ProcessParameterUtils {

	private static final String CONFIG_FILE = "configuration.properties"

	static String getRequiredParameter(
		RestAPIContext context,
		String parameterName
	) {
		return getRequiredParameters(
			context,
			parameterName
		)[parameterName]
	}

	static Map<String, String> getRequiredParameters(
		RestAPIContext context,
		String... parameterNames
	) {
		if (context == null) {
			throw new IllegalArgumentException(
				"El contexto de la REST API es obligatorio"
			)
		}

		if (parameterNames == null || parameterNames.length == 0) {
			throw new IllegalArgumentException(
				"Debe especificarse al menos un parámetro"
			)
		}

		Properties props = loadProperties(
			CONFIG_FILE,
			context.getResourceProvider()
		)

		String processName = getRequiredProperty(props, "process.name")

		String processVersion = getRequiredProperty(props, "process.version")

		ProcessAPI processAPI = context.getApiClient().getProcessAPI()

		long processDefinitionId =
			processAPI.getProcessDefinitionId(
				processName,
				processVersion
			)

		Map<String, String> result = [:]

		parameterNames.each { String parameterName ->

			if (!parameterName?.trim()) {
				throw new IllegalArgumentException(
					"El nombre del parámetro no puede estar vacío"
				)
			}

			ParameterInstance parameter =
				processAPI.getParameterInstance(
					processDefinitionId,
					parameterName
				)

			if (parameter == null || parameter.getValue() == null) {

				throw new IllegalStateException(
					"El parámetro '${parameterName}' no está configurado"
				)
			}

			String value = parameter.getValue().toString().trim()

			if (value.isEmpty()) {
				throw new IllegalStateException(
					"El parámetro '${parameterName}' está vacío"
				)
			}

			result[parameterName] = value
		}

		return result
	}

	private static String getRequiredProperty(
		Properties props,
		String propertyName
	) {
		String value =
			props.getProperty(propertyName)?.trim()

		if (!value) {
			throw new IllegalStateException(
				"La propiedad '${propertyName}' no está configurada"
			)
		}

		return value
	}

	private static Properties loadProperties(
		String fileName,
		ResourceProvider resourceProvider
	) {
		if (resourceProvider == null) {
			throw new IllegalStateException(
				"ResourceProvider no está disponible"
			)
		}

		InputStream stream =
			resourceProvider.getResourceAsStream(fileName)

		if (stream == null) {
			throw new IllegalStateException(
				"No se encontró el archivo '${fileName}'"
			)
		}

		Properties props = new Properties()

		stream.withCloseable { InputStream input ->
			props.load(input)
		}

		return props
	}

	private ProcessParameterUtils() {
		// Utility class
	}
}