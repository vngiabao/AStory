package com.ph.core.story.configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ph.core.story.models.Role;
import com.ph.core.story.models.Users;
import com.ph.core.story.repositories.UsersRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.*;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.*;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

@Configuration
public class AuthorizationServerConfig {

        private final UsersRepository usersRepository;

        public AuthorizationServerConfig(UsersRepository usersRepository) {
                this.usersRepository = usersRepository;
        }

        @Bean
        public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate,
                        PasswordEncoder passwordEncoder, AuthServerProperties props) {

                JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);

                /**
                 * ======================================================================================
                 * 
                 * 1️. SPA CLIENT (Authorization Code + PKCE)
                 * 
                 * ======================================================================================
                 */
                var spa = props.getClients().getSpa();

                // Fix logout SSO: neu client SPA da ton tai thi van cap nhat lai
                // redirect/logout URI
                // de tranh truong hop DB giu cau hinh cu sau khi service restart
                RegisteredClient existingSpaClient = repository.findByClientId(spa.getClientId());

                RegisteredClient.Builder spaClientBuilder = existingSpaClient != null
                                ? RegisteredClient.from(existingSpaClient)
                                // ID nội bộ của client trong DB (không phải client_id gửi từ frontend)

                                : RegisteredClient.withId(UUID.randomUUID().toString());

                RegisteredClient spaClient =
                                // Định danh của SPA (React / Angular / Vue app)
                                spaClientBuilder
                                                .clientId(spa.getClientId())

                                                // SPA là  public client → không có client_secret
                                                // Vì code chạy trên browser nên không thể giữ bí­ mậ­t
                                                // secret
                                                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)

                                                // Chỉ cho phép Authorization Code Flow
                                                // Flow: redirect login → nhậ­n authorization code → đổi code
                                                // lấy
                                                // token
                                                .authorizationGrantType(
                                                                AuthorizationGrantType.AUTHORIZATION_CODE)

                                                // Không cấu hình REFRESH_TOKEN grant
                                                // → SPA sẽ không được phép dùng refresh_token

                                                // URL callback sau khi login thành công
                                                // Server sẽ redirect về đây kèm theo ?code=...
                                                .redirectUris(uris -> {
                                                        uris.clear();
                                                        uris.add(spa.getRedirectUri());
                                                })

                                                // Scope dùng OpenID Connect (để có id_token)
                                                .scopes(scopes -> {
                                                        scopes.clear();
                                                        scopes.add(OidcScopes.OPENID);
                                                        // Cho phép lấy thông tin cơ bản của user (name, email,...)
                                                        scopes.add(OidcScopes.PROFILE);
                                                })

                                                // Fix logout SSO: dang ky post logout redirect URI de auth server
                                                // quay ve dung man hinh login cua app sau khi xoa session
                                                .postLogoutRedirectUris(uris -> {
                                                        uris.clear();
                                                        uris.add(spa.getLogoutRedirectUri());
                                                })

                                                // Không có scope offline_access
                                                // → Hệ thống sẽ không cấp refresh_token

                                                // Cấu hình thời gian sống của access token
                                                .tokenSettings(TokenSettings.builder()

                                                                // Access token sống theo cấu hình (ví dụ 15
                                                                // phút)
                                                                // Hết hạn → user phải login lại
                                                                .accessTokenTimeToLive(
                                                                                Duration.ofMinutes(spa
                                                                                                .getAccessTokenMinutes()))

                                                                .build())

                                                .clientSettings(ClientSettings.builder()

                                                                // Bắt buộc dùng PKCE
                                                                // SPA phải gửi code_challenge và code_verifier
                                                                // Đây là lớp bảo vệ thay thế cho client_secret
                                                                .requireProofKey(true)

                                                                // Không hiển thị màn hình consent
                                                                // Phù hợp hệ thống nội bộ
                                                                .requireAuthorizationConsent(false)

                                                                .build())

                                                .build();

                // Lưu cấu hình SPA vào bảng oauth2_registered_client
                repository.save(spaClient);

                /**
                 * ======================================================================================
                 * 
                 * 2. M2M CLIENT (Client Credentials)
                 * 
                 * ======================================================================================
                 */
                var m2m = props.getClients().getM2m();

                // Nếu có cấu hình m2m và client chưa tồn tại trong DB thì mới tạo
                if (m2m != null && repository.findByClientId(m2m.getClientId()) == null) {

                        RegisteredClient m2mClient =
                                        // ID nội bộ của client trong database (không phải client_id dùng khi gọi API)
                                        RegisteredClient.withId(UUID.randomUUID().toString())

                                                        // Định danh của service (client_id)
                                                        .clientId(m2m.getClientId())

                                                        // Mật khẩu của service (được mã hóa trước khi lưu DB)
                                                        // Vì có secret → đây là confidential client
                                                        .clientSecret(passwordEncoder.encode(m2m.getClientSecret()))

                                                        // Service sẽ xác thực bằng:
                                                        // Authorization: Basic base64(client_id:client_secret)
                                                        .clientAuthenticationMethod(
                                                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC)

                                                        // Cho phép sử dụng Client Credentials Flow
                                                        // Flow này không có user login
                                                        // Service tự dùng client_id + secret để lấy access_token
                                                        .authorizationGrantType(
                                                                        AuthorizationGrantType.CLIENT_CREDENTIALS)

                                                        // Scope mà service này được phép truy cập
                                                        // Ví dụ: chỉ có quyền đọc dữ liệu
                                                        .scope("read")

                                                        // Cấu hình thời gian sống của access token
                                                        .tokenSettings(TokenSettings.builder()

                                                                        // Access token có hiệu lực 30 phút
                                                                        // Vì là M2M nên thường không cần refresh token
                                                                        .accessTokenTimeToLive(
                                                                                        Duration.ofMinutes(m2m
                                                                                                        .getAccessTokenMinutes()))
                                                                        // Build setting
                                                                        .build())

                                                        // Không cần màn hình consent vì không có user
                                                        .clientSettings(ClientSettings.builder()
                                                                        .requireAuthorizationConsent(false).build())

                                                        .build();

                        // Lưu cấu hình client vào bảng oauth2_registered_client
                        repository.save(m2mClient);
                }

                /**
                 * ======================================================================================
                 * 
                 * 3.MOBILE CLIENT (Authorization Code + PKCE + Refresh Token)
                 * 
                 * ======================================================================================
                 */
                var mobile = props.getClients().getMobile();

                // Fix logout SSO: mobile client can dong bo lai redirect_uris trong DB
                // de callback/login/logout bang custom scheme khong bi mac cau hinh cu
                if (mobile != null && !mobile.getEffectiveRedirectUris().isEmpty()) {

                        RegisteredClient existingMobileClient = repository.findByClientId(mobile.getClientId());

                        RegisteredClient.Builder mobileClientBuilder = existingMobileClient != null
                                        ? RegisteredClient.from(existingMobileClient)
                                        // ID nội bộ của client trong database (không phải client_id gửi từ app)
                                        : RegisteredClient.withId(UUID.randomUUID().toString());

                        RegisteredClient mobileClient =
                                        // định danh của mobile app (client_id)
                                        mobileClientBuilder
                                                        .clientId(mobile.getClientId())
                                                        // Mật khẩu của mobile app (được mã hóa trước khi lưu DB)
                                                        // Vì có secret → đây là confidential client
                                                        .clientSecret(existingMobileClient != null
                                                                        ? existingMobileClient.getClientSecret()
                                                                        : passwordEncoder.encode(
                                                                                        mobile.getClientSecret()))

                                                        // CÃ¡i nÃ y gá»­i lÃªn thÃ¬ client_secret trong body
                                                        // .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)

                                                        // Cách mobile app xác thực khi gọi /token
                                                        // Sẽ gửi header:
                                                        // Authorization: Basic base64(client_id:client_secret)
                                                        .clientAuthenticationMethod(
                                                                        ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                        // Cho phép sử­ dụng Authorization Code Flow
                                                        // Flow: login → nhận code → đổi code lấy token
                                                        .authorizationGrantTypes(grantTypes -> {
                                                                grantTypes.clear();
                                                                grantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                                                                // Cho phép dùng refresh_token để lấy access_token mới
                                                                grantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                                                        })
                                                        // URL callback của mobile app
                                                        // Sau khi login thành công, server sẽ redirect về đây kèm theo
                                                        // authorization code
                                                        .redirectUris(uris -> {
                                                                uris.clear();
                                                                uris.addAll(mobile.getEffectiveRedirectUris());
                                                        })
                                                        // Scope bắt buộc nếu dùng OpenID Connect (để cấp id_token)
                                                        .scopes(scopes -> {
                                                                scopes.clear();
                                                                scopes.add(OidcScopes.OPENID);
                                                                // Cho phép lấy thông tin người dùng (name, email,...)
                                                                scopes.add(OidcScopes.PROFILE);
                                                                // Scope yêu cầu cấp refresh token (offline access)
                                                                scopes.add("offline_access");
                                                                // .scope("read")
                                                        })

                                                        // Cấu hình thời gian sống của token
                                                        .tokenSettings(TokenSettings.builder()
                                                                        // Access token có hiệu lực 15 phút
                                                                        .accessTokenTimeToLive(
                                                                                        Duration.ofMinutes(mobile
                                                                                                        .getAccessTokenMinutes()))
                                                                        // Refresh token có hiệu lực 30 ngày
                                                                        .refreshTokenTimeToLive(
                                                                                        Duration.ofDays(mobile
                                                                                                        .getRefreshTokenDays()))
                                                                        // Mỗi lần refresh sẽ cấp refresh token mới
                                                                        // (rotation)
                                                                        // Tăng bảo mật, tránh reuse token cũ
                                                                        .reuseRefreshTokens(false).build())

                                                        // Cấu hình bảo mật cho client
                                                        .clientSettings(ClientSettings.builder()
                                                                        // Bắt buộc sử dụng PKCE
                                                                        // Mobile app phải gửi code_challenge &
                                                                        // code_verifier
                                                                        .requireProofKey(true)
                                                                        // Không hiển thị màn hình yêu cầu user "Approve
                                                                        // / Consent"
                                                                        // Phù hợp hệ thống nội bộ
                                                                        .requireAuthorizationConsent(false)
                                                                        // build setting
                                                                        .build())

                                                        .build();

                        // Lưu cấu hình client vào bảng oauth2_registered_client
                        repository.save(mobileClient);
                }

                return repository;
        }

        /**
         * OAuth2 Authorization Service (lưu token vào DB)
         * 
         * @param jdbcTemplate
         * @param registeredClientRepository
         * @return
         */
        @Bean
        public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate,
                        RegisteredClientRepository registeredClientRepository, ObjectMapper objectMapper) {

                JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate,
                                registeredClientRepository);

                // Row mapper
                JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(
                                registeredClientRepository);

                rowMapper.setObjectMapper(objectMapper);

                // Parameters mapper
                JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper parametersMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper();

                parametersMapper.setObjectMapper(objectMapper);

                // set vào service
                service.setAuthorizationRowMapper(rowMapper);
                service.setAuthorizationParametersMapper(parametersMapper);

                return service;
        }

        @Bean
        public ObjectMapper objectMapper() {
                ObjectMapper mapper = new ObjectMapper();

                ClassLoader classLoader = getClass().getClassLoader();

                mapper.registerModules(SecurityJackson2Modules.getModules(classLoader));

                mapper.registerModule(new OAuth2AuthorizationServerJackson2Module());

                return mapper;
        }

        /**
         * Authorization Server Settings
         * 
         * @param props
         * @return
         */
        @Bean
        public AuthorizationServerSettings authorizationServerSettings(AuthServerProperties props) {

                return AuthorizationServerSettings.builder().issuer(props.getIssuer()).build();
        }

        /**
         * Custom jwt token, chỉ dành cho các login bằng user (authorization_code)
         * 
         * @return
         */
        // @Bean
        // public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(
        // SecurityServerProperties props) {
        // return context -> {

        // if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {

        // Authentication authentication = context.getPrincipal();

        // // Trường hợp login bằng user (authorization_code)
        // if (authentication.getPrincipal() instanceof UserDetails userDetails) {

        // String username = userDetails.getUsername();

        // Users user =
        // usersRepository.findByUsernameIgnoreCase(username).orElseThrow();

        // // 1. add audience
        // context.getClaims().audience(new java.util.ArrayList<>(
        // java.util.Collections.singletonList(props.getJwt().getAudience())));

        // // 2. add roles
        // var roles = user.getRoles().stream().map(Role::getName)
        // .collect(java.util.stream.Collectors.toList());

        // context.getClaims().claim("roles", roles);

        // // 3. add user_type
        // context.getClaims().claim("user_type", user.getUserType().name());

        // // 4. add user_id
        // context.getClaims().claim("user_id", String.valueOf(user.getId()));
        // }
        // }
        // };
        // }

        @Bean
        public OAuth2TokenCustomizer<JwtEncodingContext> jwtTokenCustomizer(
                        SecurityServerProperties props) {

                return context -> {

                        if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {

                                Authentication authentication = context.getPrincipal();

                                // CASE 1: USER (SPA / MOBILE)
                                if (authentication.getPrincipal() instanceof UserDetails userDetails) {

                                        String username = userDetails.getUsername();

                                        Users user = usersRepository.findByUsernameIgnoreCase(username).orElseThrow();

                                        // audience
                                        // context.getClaims().audience(List.of(props.getJwt().getAudience()));
                                        context.getClaims().audience(
                                                        new ArrayList<>(Arrays.asList(props.getJwt().getAudience())));

                                        // roles
                                        // var roles = user.getRoles().stream().map(Role::getName).toList();
                                        var roles = user.getRoles().stream()
                                                        .map(Role::getName)
                                                        .collect(Collectors.toList());

                                        context.getClaims().claim("roles", roles);
                                        context.getClaims().claim("user_type", user.getUserType().name());
                                        context.getClaims().claim("user_id", String.valueOf(user.getId()));
                                        context.getClaims().claim("email_verified", user.isEmailVerified());
                                }

                                // CASE 2: M2M (client_credentials)
                                else {

                                        // principal là client
                                        String clientId = context.getRegisteredClient().getClientId();

                                        // SET audience cho M2M
                                        // context.getClaims().audience(List.of(props.getJwt().getAudience()));
                                        context.getClaims().audience(
                                                        new ArrayList<>(Arrays.asList(props.getJwt().getAudience())));

                                        // (optional) add client_id vào claim
                                        context.getClaims().claim("client_id", clientId);
                                }
                        }
                };
        }
}
