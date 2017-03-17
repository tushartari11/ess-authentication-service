package de.vitapublic.essAuthenticationService.security.config;

import de.vitapublic.essAuthenticationService.common.ErrorResponse;
import de.vitapublic.essAuthenticationService.security.authentication.BasicHttpAuthenticationProvider;
import de.vitapublic.essAuthenticationService.security.authentication.JwtAuthenticationProvider;
import de.vitapublic.essAuthenticationService.security.authentication.extractor.TokenExtractor;
import de.vitapublic.essAuthenticationService.security.authentication.verifier.JwtTokenAuthenticationProcessingFilter;
import de.vitapublic.essAuthenticationService.security.authentication.verifier.SkipPathRequestMatcher;
import de.vitapublic.essAuthenticationService.security.authorization.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("de.vitapublic.essAuthenticationService.security.authentication")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    public static final String JWT_TOKEN_HEADER_PARAM = "Authorization";
    @Autowired
    @Qualifier("security.authentication.jwtAuthenticationFailureHandler")
    private AuthenticationFailureHandler failureHandler;
    @Autowired
    @Qualifier("security.authentication.extractor.jwtHeaderTokenExtractor")
    private TokenExtractor tokenExtractor;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    @Qualifier("security.authentication.jwtAuthenticationProvider")
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @Autowired
    @Qualifier("security.authentication.basicHttpAuthenticationProvider")
    private BasicHttpAuthenticationProvider basicHttpAuthenticationProvider;

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        // Admin not bigger than User
        roleHierarchy.setHierarchy(
            Roles.ROLE_ESS_USER + " > " + Roles.ROLE_ESS_CLIENT + " " +
            Roles.ROLE_ESS_TOURNAMENT_ADMIN + " > " + Roles.ROLE_ESS_CLIENT + " " +
            Roles.ROLE_ESS_SUPER_ADMIN + " > " + Roles.ROLE_ESS_TOURNAMENT_ADMIN
        );
        return roleHierarchy;
    }

    /**
     * Configure authentication
     * Set specific authorization of endpoints
     *
     * @param http      HttpSecurity
     * @return void
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
        configureAuthorizationForSwagger(http);
        configureAuthorizationForEndpoints(http);
        http.addFilterBefore(buildJwtTokenAuthenticationProcessingFilter("/**"), UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.eraseCredentials(false);
        auth.authenticationProvider(jwtAuthenticationProvider);
        auth.authenticationProvider(basicHttpAuthenticationProvider);
    }

    protected JwtTokenAuthenticationProcessingFilter buildJwtTokenAuthenticationProcessingFilter(String path) throws Exception {
        SkipPathRequestMatcher matcher = new SkipPathRequestMatcher(Arrays.asList("/documentation/**", "/swagger-ui/dist/**"), path);
        JwtTokenAuthenticationProcessingFilter filter
                = new JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }

    private void configureAuthorizationForEndpoints(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll();
//        .access("hasAnyRole('" + Roles.ROLE_ESS_SUPER_ADMIN + "," + Roles.ROLE_ESS_SYSTEM + "')");
    }

    private void configureAuthorizationForSwagger(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/documentation/**", "/swagger-ui/dist/**")
                .permitAll();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, authException) -> {
            ErrorResponse.headers(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access is denied");
        };
    }
}