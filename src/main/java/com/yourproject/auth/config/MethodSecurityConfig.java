package com.yourproject.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

/**
 * Why these configurations?
 *
 * @EnableResourceServer adds ability to check the OAuth2 tokens.
 * @EnableGlobalMethodSecurity(prePostEnabled = true) makes sure that @PreAuthorize annotation works.
 *
 * It's important that these 2 annotations come together on this config for #oauth2.hasScope to work or else just hasAuthority() will work.
 *
 * !!!IMPORTANT!!! to enable Oauth2 authentication {@link @EnableResourceServer} annotation should be used or else just basic oauth will work.
 * As well as {@link @EnableWebSecurity} should go together somewhere in application to filter the requests.
 */
@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}
