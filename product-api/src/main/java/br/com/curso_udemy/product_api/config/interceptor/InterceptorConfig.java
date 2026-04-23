package br.com.curso_udemy.product_api.config.interceptor;
import br.com.curso_udemy.product_api.config.interceptor.AuthInterceptor;
import br.com.curso_udemy.product_api.module.jwt.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    //rever
    /*
    @Bean
    public JwtService jwtService() {
        return new JwtService();
    }
    */


    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor());
    }
}