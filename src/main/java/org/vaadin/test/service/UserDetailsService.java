package org.vaadin.test.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vaadin.test.model.User;

/**
 * This class represents your business service class, responsible for obtaining the user from the database
 * 
 * @author Martin Lopez
 *
 */
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// in this part you should find a suitable user from database. The returned object must implement the UserDetails interface.
		// the original password should not be stored, instead of that, an MD5 hash of it should be used as a replacement.
		
		return new User("admin", "admin");
	}

}
