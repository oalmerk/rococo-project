package olmerk.rococo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.web.authentication.OidcLogoutAuthenticationSuccessHandler;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import olmerk.rococo.config.keys.KeyManager;
import olmerk.rococo.service.OidcCookiesLogoutAuthenticationSuccessHandler;
import olmerk.rococo.service.SpecificRequestDumperFilter;
import olmerk.rococo.service.cors.CorsCustomizer;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RococoAuthServiceConfig {

    private final KeyManager keyManager;
    private final String rococoFrontUri;
    private final String rococoAuthUri;
    private final String webClientId;
    private final CorsCustomizer corsCustomizer;
    private final String serverPort;
    private final String defaultHttpsPort = "443";
    private final Environment environment;

    @Autowired
    public RococoAuthServiceConfig(KeyManager keyManager,
                                   @Value("${rococo-front.base-uri}") String rococoFrontUri,
                                   @Value("${rococo-auth.base-uri}") String rococoAuthUri,
                                   @Value("${oauth2.web-client-id}") String webClientId,
                                   @Value("${server.port}") String serverPort,
                                   CorsCustomizer corsCustomizer,
                                   Environment environment) {
        this.keyManager = keyManager;
        this.rococoFrontUri = rococoFrontUri;
        this.rococoAuthUri = rococoAuthUri;
        this.webClientId = webClientId;
        this.serverPort = serverPort;
        this.corsCustomizer = corsCustomizer;
        this.environment = environment;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      LoginUrlAuthenticationEntryPoint loginEntryPoint) throws Exception {
        // настройки OAuth2 сервера
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer.authorizationServer();

        if (environment.acceptsProfiles(Profiles.of("local"))) {
            http.addFilterBefore(new SpecificRequestDumperFilter(
                    new RequestDumperFilter(),
                    "/login", "/connect/logout", "/oauth2/.*"
            ), DisableEncodeUrlFilter.class);
        }

        http
                // применяет цепочку только если Matcher совпадают  Только OAuth2 endpoints
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                // очистка "XSRF-TOKEN" при logout
                .with(authorizationServerConfigurer, authorizationServer ->
                        authorizationServer.oidc(oidc ->
                                oidc.logoutEndpoint(
                                        logout ->
                                                logout.logoutResponseHandler(
                                                        new OidcCookiesLogoutAuthenticationSuccessHandler(
                                                                new OidcLogoutAuthenticationSuccessHandler(),
                                                                "XSRF-TOKEN"
                                                        )
                                                )
                                )
                        )
                )
                // аутентификации для OAuth2 endpoints
                /* /oauth2/authorize
                   /oauth2/token
                   /oauth2/jwks
                   /connect/logout
                   /userinfo    */
                .authorizeHttpRequests(authorize ->
                        authorize.anyRequest().authenticated()
                )
                //настраивает обработку исключений аутентификации и авторизации
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error")
                        // при отсутствии прав возвращаемся на страницу loginEntryPoint при запросе через html/text
                        .defaultAuthenticationEntryPointFor(
                                loginEntryPoint,
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                //настраивает поведение при невалидной или истекшей сессии
                .sessionManagement(sm -> sm.invalidSessionUrl("/login"));

        corsCustomizer.corsCustomizer(http);
        return http.build();
    }


    @Bean
    @Profile({"local", "docker", "test"})
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttp() {
        return new LoginUrlAuthenticationEntryPoint("/login");
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcOperations jdbcOperations) {
        RegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcOperations);
        RegisteredClient webClient = registeredClientRepository.findByClientId(webClientId);
        if (webClient == null) {
            registeredClientRepository.save(
                    registeredClient(
                            webClientId,
                            rococoFrontUri + Callbacks.Web.login,
                            rococoFrontUri + Callbacks.Web.logout
                    )
            );
        }
        return registeredClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService jdbcOAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                                                     RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(
                jdbcOperations,
                registeredClientRepository
        );
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcOperations jdbcOperations,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcOperations, registeredClientRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(rococoAuthUri)
                .build();
    }

    @Bean
    public DefaultCookieSerializerCustomizer defaultCookieSerializerCustomizer() {
        return cookieSerializer -> {
            cookieSerializer.setUseBase64Encoding(false);
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        JWKSet set = new JWKSet(keyManager.rsaKey());
        return (jwkSelector, securityContext) -> jwkSelector.select(set);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                context.getClaims().expiresAt(Instant.now().plus(60, ChronoUnit.MINUTES));
            }
        };
    }

    private RegisteredClient registeredClient(String clientId, String redirectUri, String logoutRedirectUri) {
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        // ВКЛЮЧАЕМ PKCE
                        .requireProofKey(true)
                        .build()
                )
                .postLogoutRedirectUri(logoutRedirectUri)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.of(60, ChronoUnit.MINUTES))
                        .authorizationCodeTimeToLive(Duration.of(10, ChronoUnit.SECONDS))
                        .build())
                .build();
    }
}
