package com.api_mongo.api_mongodb_query_money.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.auth0.jwt.*;
import com.auth0.jwt.algorithms.Algorithm;

public class Auth_valid extends BasicAuthenticationFilter{

    private final String HEADER_ATRIBUTO = "Authorization";
    private final String ATRIBUTO_PREFIXO = "Bearer ";
    
    private final Auth_token service_token;
    
    public Auth_valid(AuthenticationManager authenticationManager, Auth_token service_token) {
        super(authenticationManager);
        this.service_token = service_token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                String atributo = request.getHeader(HEADER_ATRIBUTO);

                if (atributo == null) {
                    chain.doFilter(request, response);
                    return;
                }
        
                if (!atributo.startsWith(ATRIBUTO_PREFIXO)) {
                    chain.doFilter(request, response);
                    return;
                }
        
                String token = atributo.replace(ATRIBUTO_PREFIXO, "");
                UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
        
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                chain.doFilter(request, response);
            }
        
            private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        
                String email = JWT.require(Algorithm.HMAC512(service_token.TOKEN_SENHA))
                        .build()
                        .verify(token)
                        .getSubject();
                System.out.println("Enviou a request e teve acesso :"+email);
                if (email == null) {
                    return null;
                }
        
                return new UsernamePasswordAuthenticationToken(email,null, new ArrayList<>());
            }
    
    
}
