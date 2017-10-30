package org.vaadin.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * This is the UI for the form login page.
 * 
 * @author Martin Lopez
 *
 */
@SuppressWarnings("serial")
@Theme("mytheme")
@SpringUI(path = "/login")
public class LoginUI extends UI {
	
	@Autowired
    private AuthenticationProvider authenticationProvider;
	
	@Autowired
	SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @Override
    protected void init(final VaadinRequest request) {
    	
    	if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken))
    	{
    		Page.getCurrent().setLocation("/app");
    		return;
    	}
    	
    	VerticalLayout vl = new VerticalLayout();
    	LoginForm lf = new LoginForm();
    	lf.addLoginListener(e -> {
            final Authentication auth = new UsernamePasswordAuthenticationToken(e.getLoginParameter("username"), e.getLoginParameter("password"));
            try {
            	// this is the code for achieving the spring security authentication in a programmatic way
                final Authentication authenticated = authenticationProvider.authenticate(auth);
                SecurityContextHolder.getContext().setAuthentication(authenticated);
                sessionAuthenticationStrategy.onAuthentication(auth, ((VaadinServletRequest)VaadinService.getCurrentRequest()).getHttpServletRequest(), ((VaadinServletResponse)VaadinService.getCurrentResponse()).getHttpServletResponse());
                Page.getCurrent().setLocation("/app");
            } catch (final AuthenticationException ex) {
            	String message = "Incorrect user or password";
            	Notification.show(message, Notification.Type.ERROR_MESSAGE);
            }

    	});
    	
    	vl.addComponent(lf);
    	vl.setComponentAlignment(lf, Alignment.MIDDLE_CENTER);
    	vl.setSizeFull();
    	setContent(vl);
    }

}
