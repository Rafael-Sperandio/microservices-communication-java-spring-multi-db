package br.com.curso_udemy.product_api.module.jwt.service;


import br.com.curso_udemy.product_api.config.exceptions.AuthenticationException;
import br.com.curso_udemy.product_api.module.jwt.dto.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Strings;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
public class JwtService {

    private static final String EMPTY_SPACE = " ";
    private static final Integer TOKEN_INDEX = 1;
    @Value("${app-config.secrets.api-secret}")
    private String apiSecret;

    public void validateAuthorization(String token){
        var accesstoken = extractToken(token);
        try{

            var claims = Jwts
                    .parser()
                    .verifyWith(Keys.hmacShaKeyFor(apiSecret.getBytes()))
                    .build()
                    .parseSignedClaims(accesstoken)
                    .getPayload();


            JwtResponse user = JwtResponse.getUser(claims);

            if(isEmpty(user) || isEmpty(user.getId())){
                throw  new AuthenticationException("this user is not valid.");
            }

        }catch (Exception ex){
            ex.printStackTrace();
            throw  new AuthenticationException("Error while trying to proccess the Acccess Token.");
        }
    }
    private String extractToken(String token){
        if (isEmpty(token)) {
            throw new AuthenticationException("The access token was not informed.");
        }
        if (token.contains(EMPTY_SPACE)) {
            return token.split(EMPTY_SPACE)[TOKEN_INDEX];
        }
        return token;
    }



}
