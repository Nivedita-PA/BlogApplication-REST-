package com.mountblue.BlogApplication.AuthConfig;
import com.mountblue.BlogApplication.Security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class AuthConfiguration {

@Autowired
private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/loginUser","/postsPages","/post/**","/posts",
                                "updatePost/**","/logout","/deletePost/**","/addComment/**",
                                "/allComments/**","/updateComment/**","/deleteComment/**",
                                "/filterByPublishedAt","/searchPosts","/posts/sort/asc",
                                "/posts/sort/desc","/getFilteredPosts","/addComment/**"
                                ,"/allComments/**")
                        .permitAll()
                ).httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.disable());

        return http.build();
    }

}
