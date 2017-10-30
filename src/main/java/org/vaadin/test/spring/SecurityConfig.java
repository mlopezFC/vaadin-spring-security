package org.vaadin.test.spring;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;

import com.vaadin.spring.annotation.EnableVaadin;

/**
 * This class is the main security configuration class. For more information of
 * how to configure the security, go to
 * https://docs.spring.io/spring-security/site/docs/current/guides/html5//helloworld-javaconfig.html#creating-your-spring-security-configuration
 * 
 * @author Martin Lopez
 *
 */
@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = { "org.vaadin.test" })
@EnableVaadin
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
			.csrf()
			.disable()
			.authorizeRequests()
			.antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login**", "/login/**").permitAll()
			.anyRequest().authenticated().antMatchers("/app").access("hasAuthority('USE-APP')").and().formLogin()
			.loginPage("/login?auth").permitAll().defaultSuccessUrl("/app", true).and()
			.sessionManagement().sessionAuthenticationStrategy(sessionControlAuthenticationStrategy());
	}
	
    @Bean
    public SessionAuthenticationStrategy sessionControlAuthenticationStrategy(){
        SessionFixationProtectionStrategy sessionFixationProtectionStrategy = new SessionFixationProtectionStrategy();
        sessionFixationProtectionStrategy.setMigrateSessionAttributes(false);

        RegisterSessionAuthenticationStrategy registerSessionAuthenticationStrategy = new RegisterSessionAuthenticationStrategy(sessionRegistry());

        List<SessionAuthenticationStrategy> strategies = new LinkedList<>();
        strategies.add(sessionFixationProtectionStrategy);
        strategies.add(registerSessionAuthenticationStrategy);

        CompositeSessionAuthenticationStrategy compositeSessionAuthenticationStrategy = new CompositeSessionAuthenticationStrategy(strategies);

        return compositeSessionAuthenticationStrategy;
    }

	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}

	public PasswordEncoder encoder() {
		return NoOpPasswordEncoder.getInstance();
	}

}