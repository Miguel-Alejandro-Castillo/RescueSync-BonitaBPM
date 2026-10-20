package ar.com.rescuesync.rest.api;

import groovy.json.JsonBuilder
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.apache.http.HttpHeaders
import org.bonitasoft.engine.api.ProcessAPI
import org.bonitasoft.engine.bpm.parameter.ParameterInstance
import org.bonitasoft.web.extension.ResourceProvider
import org.bonitasoft.web.extension.rest.RestApiResponse
import org.bonitasoft.web.extension.rest.RestApiResponseBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.bonitasoft.web.extension.rest.RestAPIContext
import org.bonitasoft.web.extension.rest.RestApiController
import java.time.LocalDate

class Index implements RestApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Index.class)

    @Override
    RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder responseBuilder, RestAPIContext context) {
        // To retrieve query parameters use the request.getParameter(..) method.
        // Be careful, parameter values are always returned as String values

        // Here is an example of how you can retrieve configuration parameters from a properties file
        // It is safe to remove this if no configuration is required
        Properties props = loadProperties("configuration.properties", context.resourceProvider)
        String paramValue = props["myParameterKey"]

		String pathInfo = request.getPathInfo() ?: ""
		String method = request.getMethod()

        // Prepare the result
        def result = [  "myParameterKey" : paramValue, "currentDate" : LocalDate.now().toString() ]

        // Send the result as a JSON representation
        // You may use buildPagedResponse if your result is multiple
        return buildResponse(responseBuilder, HttpServletResponse.SC_OK, new JsonBuilder(result).toString())
    }

    /**
     * Build an HTTP response.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  httpStatus the status of the response
     * @param  body the response body
     * @return a RestAPIResponse
     */
    RestApiResponse buildResponse(RestApiResponseBuilder responseBuilder, int httpStatus, Serializable body) {
        return responseBuilder.with {
            withResponseStatus(httpStatus)
            withResponse(body)
            build()
        }
    }

    /**
     * Returns a paged result like Bonita BPM REST APIs.
     * Build a response with a content-range.
     *
     * @param  responseBuilder the Rest API response builder
     * @param  body the response body
     * @param  p the page index
     * @param  c the number of result per page
     * @param  total the total number of results
     * @return a RestAPIResponse
     */
    RestApiResponse buildPagedResponse(RestApiResponseBuilder responseBuilder, Serializable body, int p, int c, long total) {
        return responseBuilder.with {
            withContentRange(p,c,total)
            withResponse(body)
            build()
        }
    }

    /**
     * Load a property file into a java.util.Properties
     */
    Properties loadProperties(String fileName, ResourceProvider resourceProvider) {
        Properties props = new Properties()
        resourceProvider.getResourceAsStream(fileName).withStream { InputStream s ->
            props.load s
        }
        props
    }

}
