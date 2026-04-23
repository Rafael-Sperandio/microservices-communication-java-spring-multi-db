package br.com.curso_udemy.product_api.config.interceptor;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeingClientAuthInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION = "Authorization";
    @Override
    public void apply(RequestTemplate template){
        var currentRequest = getCurrentRequest();
        template
                .header(AUTHORIZATION,currentRequest.getHeader(AUTHORIZATION));
    }

    private HttpServletRequest getCurrentRequest(){
        try {
            return ((ServletRequestAttributes)RequestContextHolder
                    .getRequestAttributes())
                    .getRequest();
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ValidationException("The current request could not be processed.");
        }
    }
}
