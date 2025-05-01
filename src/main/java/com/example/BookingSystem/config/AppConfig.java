package com.example.BookingSystem.config;

import com.example.BookingSystem.features.user.domain.repository.UserRepository;
import com.example.BookingSystem.interceptor.SwaggerBasicAuthenticationInterceptor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.NoSuchElementException;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final UserRepository userRepository;
    private final SwaggerBasicAuthenticationInterceptor swaggerBasicAuthenticationInterceptor;

    AppConfig(UserRepository userRepository, SwaggerBasicAuthenticationInterceptor swaggerBasicAuthenticationInterceptor) {
        this.userRepository = userRepository;
        this.swaggerBasicAuthenticationInterceptor = swaggerBasicAuthenticationInterceptor;
    }

    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(swaggerBasicAuthenticationInterceptor).addPathPatterns("/swagger-ui/index.html");
    }


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper
                .getConfiguration()
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return modelMapper;
    }

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> userRepository.findByEmail(username)
                .orElseThrow(()->new NoSuchElementException("User Not Found"));
    }

}
