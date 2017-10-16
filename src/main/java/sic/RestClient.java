package sic;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestClient {

    private static final RestClient INSTANCE = new RestClient();
    private static final String PREFIX = "/api/v1";
    private static String HOST;
    private final RestTemplate restTemplate;

    private RestClient() {
        HOST = System.getenv("SIC_API");
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        restTemplate = restTemplateBuilder.rootUri(HOST + PREFIX)
                .errorHandler(new ApiResponseErrorHandler())
                //.setConnectTimeout(10000)
                //.setReadTimeout(10000)
                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .build();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AuthorizationHeaderRequestInterceptor());
        restTemplate.setInterceptors(interceptors);
    }

    //Singleton
    public static RestTemplate getRestTemplate() {
        return INSTANCE.restTemplate;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
    }
}
