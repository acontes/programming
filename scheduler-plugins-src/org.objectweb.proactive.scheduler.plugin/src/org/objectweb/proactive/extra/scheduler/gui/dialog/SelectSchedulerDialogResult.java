package org.objectweb.proactive.extra.scheduler.gui.dialog;

public class SelectSchedulerDialogResult {

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
