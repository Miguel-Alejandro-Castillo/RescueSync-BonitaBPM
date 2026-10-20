package ar.com.rescuesync.rest.api

import javax.servlet.http.HttpServletRequest
import ar.com.rescuesync.rest.api.client.ApiNacionalClient

class Recursos extends BaseApiController {
	
		@Override
		protected Object doAction(
			HttpServletRequest request,
			ApiNacionalClient client
		) {
			return client.get("/recursos")
		}
}