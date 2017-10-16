package sic;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import sic.modelo.UsuarioActivo;

public class AuthorizationHeaderRequestInterceptor implements ClientHttpRequestInterceptor {
    
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("Authorization", "Bearer " + UsuarioActivo.getInstance().getToken());
        return execution.execute(request, body);
    }
    
}
