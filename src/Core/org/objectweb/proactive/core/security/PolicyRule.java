/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.security;

import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.core.security.exceptions.IncompatiblePolicyException;
import org.objectweb.proactive.core.security.securityentity.RuleEntities;


public class PolicyRule implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8290604572288562113L;
	private RuleEntities from;
	private RuleEntities to;
	private Communication communicationReply;
	private Communication communicationRequest;
	private boolean migration;
	private boolean aocreation;

    /**
     * Default constructor, initialize a policy with communication attribute
     * sets to allowed and authentication,confidentiality and integrity set to
     * optional
     */
    public PolicyRule() {
    	this.from = new RuleEntities();
    	this.to = new RuleEntities();
    	this.communicationReply = new Communication();
    	this.communicationRequest = new Communication();
    	this.migration = false;
    	this.aocreation = false;
    }
    
    /**
     * Copy constructor.
     */
    public PolicyRule(PolicyRule policy) {
    	this.from = new RuleEntities(policy.getEntitiesFrom());
    	this.to = new RuleEntities(policy.getEntitiesTo());
    	this.communicationReply = new Communication(policy.getCommunicationReply());
    	this.communicationRequest = new Communication(policy.getCommunicationRequest());
    	this.migration = policy.isMigration();
    	this.aocreation = policy.isAocreation();
    }

    /**
     * @param object
     */
    public void setEntitiesFrom(RuleEntities entities) {
        this.from = entities;
    }

    /**
     * @param object
     */
    public void setEntitiesTo(RuleEntities entities) {
        this.to = entities;
    }

    /**
     * @param object
     */
    public void setCommunicationRulesRequest(Communication object) {
    	this.communicationRequest = object;
    }

    /**
     * @param object
     */
    public void setCommunicationRulesReply(Communication object) {
    	this. communicationReply = object;
    }

    @Override
    public String toString() {
        String vnFrom;
        String vnTo;
        vnFrom = vnTo = null;
        if (this.from == null) {
        	vnFrom = "all";
        } else {
        	vnFrom = this.from.toString();
        }
        if (this.to == null) {
        	vnTo = "all";
        } else {
        	vnTo = this.to.toString();
        }

        return vnFrom + "-->\n" + vnTo + "\nRequest : " + this.communicationRequest +
        "\nReply : " + this.communicationReply + "\nMigration :" + this.migration +
        "\nAOCreation:" + this.aocreation;
    }

//    /**
//     * @param arrayLists
//     */
//    public void setCommunicationRules(Communication[] arrayLists) {
//        setCommunicationRulesReply(arrayLists[0]);
//        setCommunicationRulesRequest(arrayLists[1]);
//    }

    public Communication getCommunicationReply() {
        return this.communicationReply;
    }

    public Communication getCommunicationRequest() {
        return this.communicationRequest;
    }

    public RuleEntities getEntitiesFrom() {
        return this.from;
    }

    public RuleEntities getEntitiesTo() {
        return this.to;
    }

    /**
     * @return true if object creation is authorized
     */
    public boolean isAocreation() {
        return this.aocreation;
    }

    /**
     * @return true if migration is authorized
     */
    public boolean isMigration() {
        return this.migration;
    }

    /**
     * @param b
     */
    public void setAocreation(boolean b) {
    	this.aocreation = b;
    }

    /**
     * @param b
     */
    public void setMigration(boolean b) {
    	this.migration = b;
    }

    public static PolicyRule mergePolicies(List<PolicyRule> policies) {
        PolicyRule resultPolicy = null;

        for (PolicyRule policy : policies) {
            int fromLevel = policy.getEntitiesFrom().getLevel();
            int toLevel = policy.getEntitiesTo().getLevel();

            if (resultPolicy == null) {
                resultPolicy = new PolicyRule(policy);
            } else {
                int resultFromLevel = resultPolicy.getEntitiesFrom().getLevel();
                int resultToLevel = resultPolicy.getEntitiesTo().getLevel();

                if (fromLevel > resultFromLevel) {
                    resultPolicy.setEntitiesFrom(policy.getEntitiesFrom());
                }
                if (toLevel > resultToLevel) {
                    resultPolicy.setEntitiesTo(policy.getEntitiesTo());
                }
                if ((policy.isAocreation() != resultPolicy.isAocreation()) ||
                        (policy.isMigration() != resultPolicy.isMigration())) {
                    throw new IncompatiblePolicyException("Incompatible rules");
                }
                resultPolicy.setCommunicationRulesReply(Communication.computeCommunication(
                        policy.getCommunicationReply(),
                        resultPolicy.getCommunicationReply()));
                resultPolicy.setCommunicationRulesRequest(Communication.computeCommunication(
                        policy.getCommunicationRequest(),
                        resultPolicy.getCommunicationRequest()));
            }
        }
        return resultPolicy;
    }
}
