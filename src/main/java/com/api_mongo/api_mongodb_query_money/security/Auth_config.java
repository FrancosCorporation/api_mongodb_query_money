package com.api_mongo.api_mongodb_query_money.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.api_mongo.api_mongodb_query_money.services.Services_data_Impl;

@EnableWebSecurity
public class Auth_config extends WebSecurityConfigurerAdapter {

    private final Services_data_Impl services_data_Impl;
    private final PasswordEncoder Encoder;
    private final Auth_token service_token;

    public Auth_config(Services_data_Impl services_data_Impl, PasswordEncoder encoder, Auth_token service_token) {
        this.services_data_Impl = services_data_Impl;
        Encoder = encoder;
        this.service_token = service_token;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(services_data_Impl).passwordEncoder(Encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/").permitAll()
                .antMatchers(HttpMethod.GET, "/login").permitAll()
                .antMatchers(HttpMethod.GET, "/src/main/resources/templates/logo.png").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/index,html").permitAll()
                .antMatchers(HttpMethod.GET, "/account").permitAll()
                .antMatchers(HttpMethod.GET, "/account2").permitAll()
                .antMatchers(HttpMethod.GET, "/register").permitAll()
                .antMatchers(HttpMethod.GET, "/forgot").permitAll()
                .antMatchers(HttpMethod.POST, "/api/client/register").permitAll()
                .antMatchers(HttpMethod.POST, "/api/client/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new Auth_filter(authenticationManager(), service_token))
                .addFilter(new Auth_valid(authenticationManager(), service_token))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
