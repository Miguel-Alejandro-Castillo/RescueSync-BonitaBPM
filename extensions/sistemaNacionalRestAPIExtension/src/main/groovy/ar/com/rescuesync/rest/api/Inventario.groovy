package ar.com.rescuesync.rest.api

import javax.servlet.http.HttpServletRequest
import ar.com.rescuesync.rest.api.client.ApiNacionalClient

class Inventario extends BaseApiController {
    @Override
    protected Object doAction(HttpServletRequest request, ApiNacionalClient client) {
        String id = request.getPathInfo().tokenize('/')[-2]
        if (!(id ==~ /[1-9][0-9]*/)) {
            throw new IllegalArgumentException('Identificador de ONG inválido')
        }
        return client.get("/ongs/${id}/recursos")
    }
}
