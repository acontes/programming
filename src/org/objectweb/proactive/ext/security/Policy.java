/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.ext.security;

import java.io.Serializable;

import java.util.ArrayList;


public class Policy implements Serializable {
    protected ArrayList from;
    protected ArrayList to;
    protected Communication communicationReply;
    protected Communication communicationRequest;
    protected boolean migration = false;
    protected boolean aocreation = false;

    /**
     * Default constructor, initialize a policy with communication attribute sets to allowed and
     * authentication,confidentiality and integrity set to optional
     */
    public Policy() {
        from = new ArrayList();
        from.add(new DefaultEntity());
        to = new ArrayList();
        to.add(new DefaultEntity());
        communicationReply = new Communication();
        communicationRequest = new Communication();
    }

    /**
     * @param object
     */
    public void setEntitiesFrom(ArrayList object) {
        from = object;
    }

    /**
     * @param object
     */
    public void setEntitiesTo(ArrayList object) {
        to = object;
    }

    /**
     * @param object
     */
    public void setCommunicationRulesRequest(Communication object) {
        communicationRequest = object;
    }

    /**
     * @param object
     */
    public void setCommunicationRulesReply(Communication object) {
        communicationReply = object;
    }

    public String toString() {
        String vnFrom;
        String vnTo;
        vnFrom = vnTo = null;
        if (from == null) {
            vnFrom = null;
        } else {
            Entity[] f = new Entity[0];
            Entity[] eF = (Entity[]) from.toArray(f);
            for (int i = 0; i < eF.length; i++)
                vnFrom = eF[i].getName() + ",";
        }
        if (to == null) {
            vnTo = null;
        } else {
            Entity[] f = new Entity[0];
            Entity[] eT = (Entity[]) to.toArray(f);
            for (int i = 0; i < eT.length; i++)
                vnTo = eT[i].getName() + ",";
        }

        return vnFrom + "-->" + vnTo + "||  Request:" + communicationRequest +
        " :: Reply : " + communicationReply + " || Migration :" + migration +
        "|| AOCreation:" + aocreation;
    }

    /**
     * @param arrayLists
     */
    public void setCommunicationRules(Communication[] arrayLists) {
        setCommunicationRulesReply(arrayLists[0]);
        setCommunicationRulesRequest(arrayLists[1]);
    }

    public Communication getCommunicationReply() {
        return communicationReply;
    }

    public Communication getCommunicationRequest() {
        return communicationRequest;
    }

    public ArrayList getEntitiesFrom() {
        return from;
    }

    public ArrayList getEntitiesTo() {
        return to;
    }

    /**
     * @return true if object creation is authorized
     */
    public boolean isAocreation() {
        return aocreation;
    }

    /**
     * @return true if migration is authorized
     */
    public boolean isMigration() {
        return migration;
    }

    /**
     * @param b
     */
    public void setAocreation(boolean b) {
        aocreation = b;
    }

    /**
     * @param b
     */
    public void setMigration(boolean b) {
        migration = b;
    }
}
