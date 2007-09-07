package org.objectweb.proactive.ic2d.security.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.security.Communication;

public class SimplePolicyRule {

	private static int num = 0;

	private String name;

	private List<String> from;

	private List<String> to;

	private boolean request;

	private int reqAuth;

	private int reqConf;

	private int reqInt;

	private boolean reply;

	private int repAuth;

	private int repConf;

	private int repInt;

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
		this.reqAuth = Communication.OPTIONAL;
		this.reqConf = Communication.OPTIONAL;
		this.reqInt = Communication.OPTIONAL;

		this.reply = true;
		this.repAuth = Communication.OPTIONAL;
		this.repConf = Communication.OPTIONAL;
		this.repInt = Communication.OPTIONAL;

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

	public int getRepAuth() {
		return this.repAuth;
	}

	public void setRepAuth(int repAuth) {
		this.repAuth = repAuth;
	}

	public int getRepConf() {
		return this.repConf;
	}

	public void setRepConf(int repConf) {
		this.repConf = repConf;
	}

	public int getRepInt() {
		return this.repInt;
	}

	public void setRepInt(int repInt) {
		this.repInt = repInt;
	}

	public boolean isReply() {
		return this.reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public int getReqAuth() {
		return this.reqAuth;
	}

	public void setReqAuth(int reqAuth) {
		this.reqAuth = reqAuth;
	}

	public int getReqConf() {
		return this.reqConf;
	}

	public void setReqConf(int reqConf) {
		this.reqConf = reqConf;
	}

	public int getReqInt() {
		return this.reqInt;
	}

	public void setReqInt(int reqInt) {
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
