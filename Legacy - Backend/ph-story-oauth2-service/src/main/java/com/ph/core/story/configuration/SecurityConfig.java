package com.ph.core.story.configuration;

import java.util.List;

import com.ph.core.story.security.CustomAuthenticationFailureHandler;
import com.ph.core.story.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class);

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher).cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint("/login"),
                        new AntPathRequestMatcher("/oauth2/authorize")));

        authorizationServerConfigurer.oidc(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
            AuthServerProperties props,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
            CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler)
            throws Exception {

        http.authorizeHttpRequests(auth -> auth
                // Cho phép các folder hoặc trang
                // Fix logout SSO: cho phep app goi endpoint logout rieng de xoa session browser
                .requestMatchers("/login", "/sso/logout", "/css/**", "/js/**", "/images/**")
                // Cho phép
                .permitAll()
                // Ngược lại
                .anyRequest()
                // Phải thực hiện authen
                .authenticated())
                // Cái này sẽ là cấu hình mặc định không xử lý được khoá,
                // mở tài khoản khi login lỗi liên tục
                // .formLogin(Customizer.withDefaults())

                // Nếu không có cái này → không hiện login form
                // Xử lý lock, unlock và tăng số lần loginfail
                .formLogin(form -> form
                        // Trang login - trang customer login
                        .loginPage("/login")
                        // URL mà form sẽ POST username/password lên
                        // Spring tự xử lý POST
                        .loginProcessingUrl("/login")
                        // Tình trạng lỗi
                        .failureUrl("/login?error=true")
                        // Xử lý khi login lỗi
                        .failureHandler(customAuthenticationFailureHandler)
                        // Xử lý khi login thành công
                        .successHandler(customAuthenticationSuccessHandler))
                // Nếu không có → browser sẽ bị CORS error
                .cors(Customizer.withDefaults()).logout(logout -> logout
                        // Mở trang logout
                        // .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        // Khi logout thành công thì quay về URI được chỉ định
                        .logoutSuccessUrl(props.getClients().getSpa().getLogoutRedirectUri())
                        // .logoutSuccessHandler((request, response, authentication) -> {
                        // response.sendRedirect("/login?logout");
                        // })
                        // Invalidate session
                        .invalidateHttpSession(true)
                        // Delete cookie
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(AppCorsProperties appCorsProperties) {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(appCorsProperties.getAllowedOrigins()); // http://localhost:*
        config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
