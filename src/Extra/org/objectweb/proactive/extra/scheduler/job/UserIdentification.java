package org.objectweb.proactive.extra.scheduler.job;

import java.io.Serializable;

/**
 * <font color="red">THIS CLASS HAS TO BE IMPLEMENTED</font>
 * This class will be able to authenticate a user.
 * For the moment it is a user/password authentification.
 * Two userIdentification are equals if there username and password are the same.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 4, 2007
 * @since ProActive 3.2
 */
public class UserIdentification implements Serializable {

	/**  */
	private static final long serialVersionUID = -1382908192253223167L;
	/** user name */
	private String username;
	/** user password */
	private String password;
	
	
	/**
	 * Constructor of user identification using user name and password.
	 * 
	 * @param username the user name.
	 * @param password the user password.
	 */
	public UserIdentification(String username, String password) {
		this.username = username;
		this.password = password;
	}


	/**
	 * To get the password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}


	/**
	 * To get the username
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * 
	 * @return true if the username and password of this and obj are equals.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserIdentification)
			return username.equals(((UserIdentification) obj).username)
					&& password.equals(((UserIdentification) obj).password);
		return false;
	}
	
}
