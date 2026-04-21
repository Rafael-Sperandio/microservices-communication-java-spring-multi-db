package br.com.curso_udemy.product_api.config.intercerptor;

import br.com.curso_udemy.product_api.config.exceptions.ValidationException;
import br.com.curso_udemy.product_api.module.jwt.service.JwtService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerInterceptor;

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
