package org.vaadin.test.spring;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * This class will initialize the spring security framework
 * 
 * @author Martin Lopez / Vaadin
 *
 */
@WebListener
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

	public SecurityWebApplicationInitializer() {
		super(SecurityConfig.class);
		
	}
	
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		super.beforeSpringSecurityFilterChain(servletContext);
		servletContext.addListener(new HttpSessionEventPublisher());
	}
	
}
