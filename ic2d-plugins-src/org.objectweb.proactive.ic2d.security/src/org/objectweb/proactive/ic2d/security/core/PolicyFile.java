package org.objectweb.proactive.ic2d.security.core;

import java.util.List;

import org.objectweb.proactive.core.security.TypedCertificate;


public class PolicyFile {
	
	private String applicationName;
	private String keystorePath;
	private List<SimplePolicyRule> rules;
	private List<TypedCertificate> authorizedUsers;
	
	public PolicyFile(String applicationName,
			String keystorePath, List<SimplePolicyRule> rules,
			List<TypedCertificate> authorizedUsers) {
		this.applicationName = applicationName;
		this.keystorePath = keystorePath;
		this.rules = rules;
		this.authorizedUsers = authorizedUsers;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public List<TypedCertificate> getAuthorizedUsers() {
		return authorizedUsers;
	}

	public String getKeystorePath() {
		return keystorePath;
	}

	public List<SimplePolicyRule> getRules() {
		return rules;
	}
}
