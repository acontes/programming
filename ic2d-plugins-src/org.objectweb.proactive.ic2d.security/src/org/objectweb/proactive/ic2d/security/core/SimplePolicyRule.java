package org.objectweb.proactive.ic2d.security.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.security.Communication.Authorization;

public class SimplePolicyRule {

	private static int num = 0;

	private String name;

	private List<String> from;

	private List<String> to;

	private boolean request;

	private Authorization reqAuth;

	private Authorization reqConf;

	private Authorization reqInt;

	private boolean reply;

	private Authorization repAuth;

	private Authorization repConf;

	private Authorization repInt;

	private boolean aoCreation;

	private boolean migration;

	public SimplePolicyRule(String name) {
		this();
		this.name = name;
		num--;
	}

	public SimplePolicyRule() {

		this.name = "Rule " + ++num;
		this.from = new ArrayList<String>();
		this.to = new ArrayList<String>();

		this.request = true;
		this.reqAuth = Authorization.OPTIONAL;
		this.reqConf = Authorization.OPTIONAL;
		this.reqInt = Authorization.OPTIONAL;

		this.reply = true;
		this.repAuth = Authorization.OPTIONAL;
		this.repConf = Authorization.OPTIONAL;
		this.repInt = Authorization.OPTIONAL;

		this.aoCreation = true;
		this.migration = true;
	}

	public void addFrom(String name) {
		if (name != null) {
			this.from.add(name);
		}
	}

	public void removeFrom(int i) {
		this.from.remove(i);
	}

	public void addTo(String name) {
		if (name != null) {
			this.to.add(name);
		}
	}

	public void removeTo(int i) {
		this.to.remove(i);
	}

	@Override
	public String toString() {
		return this.name;
	}

	public Authorization getRepAuth() {
		return this.repAuth;
	}

	public void setRepAuth(Authorization repAuth) {
		this.repAuth = repAuth;
	}

	public Authorization getRepConf() {
		return this.repConf;
	}

	public void setRepConf(Authorization repConf) {
		this.repConf = repConf;
	}

	public Authorization getRepInt() {
		return this.repInt;
	}

	public void setRepInt(Authorization repInt) {
		this.repInt = repInt;
	}

	public boolean isReply() {
		return this.reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public Authorization getReqAuth() {
		return this.reqAuth;
	}

	public void setReqAuth(Authorization reqAuth) {
		this.reqAuth = reqAuth;
	}

	public Authorization getReqConf() {
		return this.reqConf;
	}

	public void setReqConf(Authorization reqConf) {
		this.reqConf = reqConf;
	}

	public Authorization getReqInt() {
		return this.reqInt;
	}

	public void setReqInt(Authorization reqInt) {
		this.reqInt = reqInt;
	}

	public boolean isRequest() {
		return this.request;
	}

	public void setRequest(boolean request) {
		this.request = request;
	}

	public List<String> getFrom() {
		return this.from;
	}

	public List<String> getTo() {
		return this.to;
	}

	public boolean isAoCreation() {
		return this.aoCreation;
	}

	public void setAoCreation(boolean aoCreation) {
		this.aoCreation = aoCreation;
	}

	public boolean isMigration() {
		return this.migration;
	}

	public void setMigration(boolean migration) {
		this.migration = migration;
	}

}
