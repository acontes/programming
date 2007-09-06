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

		name = "Rule " + ++num;
		from = new ArrayList<String>();
		to = new ArrayList<String>();

		request = true;
		reqAuth = Communication.OPTIONAL;
		reqConf = Communication.OPTIONAL;
		reqInt = Communication.OPTIONAL;

		reply = true;
		repAuth = Communication.OPTIONAL;
		repConf = Communication.OPTIONAL;
		repInt = Communication.OPTIONAL;

		aoCreation = true;
		migration = true;
	}

	public void addFrom(String name) {
		if (name != null) {
			from.add(name);
		}
	}

	public void removeFrom(int i) {
		from.remove(i);
	}

	public void addTo(String name) {
		if (name != null) {
			to.add(name);
		}
	}

	public void removeTo(int i) {
		to.remove(i);
	}

	@Override
	public String toString() {
		return name;
	}

	public int getRepAuth() {
		return repAuth;
	}

	public void setRepAuth(int repAuth) {
		this.repAuth = repAuth;
	}

	public int getRepConf() {
		return repConf;
	}

	public void setRepConf(int repConf) {
		this.repConf = repConf;
	}

	public int getRepInt() {
		return repInt;
	}

	public void setRepInt(int repInt) {
		this.repInt = repInt;
	}

	public boolean isReply() {
		return reply;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public int getReqAuth() {
		return reqAuth;
	}

	public void setReqAuth(int reqAuth) {
		this.reqAuth = reqAuth;
	}

	public int getReqConf() {
		return reqConf;
	}

	public void setReqConf(int reqConf) {
		this.reqConf = reqConf;
	}

	public int getReqInt() {
		return reqInt;
	}

	public void setReqInt(int reqInt) {
		this.reqInt = reqInt;
	}

	public boolean isRequest() {
		return request;
	}

	public void setRequest(boolean request) {
		this.request = request;
	}

	public List<String> getFrom() {
		return from;
	}

	public List<String> getTo() {
		return to;
	}

	public boolean isAoCreation() {
		return aoCreation;
	}

	public void setAoCreation(boolean aoCreation) {
		this.aoCreation = aoCreation;
	}

	public boolean isMigration() {
		return migration;
	}

	public void setMigration(boolean migration) {
		this.migration = migration;
	}

}
