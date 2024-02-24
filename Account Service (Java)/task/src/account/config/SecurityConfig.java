package account.config;

import account.service.CustomAccessDeniedHandler;
import account.repository.AppUserRepository;
import account.service.PasswordCheck;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    @Bean
    public RequestAttributeSecurityContextRepository getRequestAttributeSecurityContextRepository() {
        return new RequestAttributeSecurityContextRepository();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public PasswordCheck passwordCheck(AppUserRepository repository){
        return new PasswordCheck(repository);
    }
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
            .csrf(configurer -> configurer.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/changepass").hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
                .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
                .requestMatchers(HttpMethod.GET, "/api/admin/user/").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/user/{email}").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.PUT, "/api/admin/user/access").hasRole("ADMINISTRATOR")
                .requestMatchers(HttpMethod.GET, "/api/security/events/").hasRole("AUDITOR")
            )
            .exceptionHandling(customizer ->
                    customizer.accessDeniedHandler(customAccessDeniedHandler())
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
    @Bean
    AccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }
}