package ar.com.rescuesync.rest.api

import javax.servlet.http.HttpServletRequest
import ar.com.rescuesync.rest.api.client.ApiNacionalClient

class Ongs extends BaseApiController {

	@Override
	protected Object doAction(HttpServletRequest request, ApiNacionalClient client) {

		String cuit = request.getParameter("cuit")

		if (cuit) {
			return client.get("/ongs?cuit=${cuit}")
		}

		return client.get("/ongs")
	}
}