package org.objectweb.proactive.extra.scheduler.gui.dialog;

import java.io.Serializable;

public class SelectSchedulerDialogResult implements Serializable {

	private static final long serialVersionUID = 2636651259848701997L;
	private String url = null;
	private String login = null;
	private String password = null;
	private Boolean logAsAdmin = false;
	
	public SelectSchedulerDialogResult(String url, String login, String password, Boolean logAsAdmin) {
		this.url = url;
		this.login = login;
		this.password = password;
		this.logAsAdmin = logAsAdmin;
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

	public Boolean isLogAsAdmin() {
		return logAsAdmin;
	}
}
