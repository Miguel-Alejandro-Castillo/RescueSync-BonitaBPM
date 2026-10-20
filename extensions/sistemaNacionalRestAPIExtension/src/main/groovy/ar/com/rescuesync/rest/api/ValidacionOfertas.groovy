package ar.com.rescuesync.rest.api

import groovy.json.JsonSlurper
import javax.servlet.http.HttpServletRequest
import ar.com.rescuesync.rest.api.client.ApiNacionalClient

class ValidacionOfertas extends BaseApiController {
    @Override
    protected Object doAction(HttpServletRequest request, ApiNacionalClient client) {
        def solicitud = new JsonSlurper().parse(request.getReader())
        return client.post('/validaciones/ofertas', solicitud)
    }
}
