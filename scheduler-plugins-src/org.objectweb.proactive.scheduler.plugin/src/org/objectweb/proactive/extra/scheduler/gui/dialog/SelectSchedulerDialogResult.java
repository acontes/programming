package org.objectweb.proactive.extra.scheduler.gui.dialog;

import java.io.Serializable;

public class SelectSchedulerDialogResult implements Serializable {

	private static final long serialVersionUID = 2636651259848701997L;
	private String url = null;
	private String login = null;
	private String password = null;
	
	public SelectSchedulerDialogResult(String url, String login, String password) {
		this.url = url;
		this.login = login;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}
