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
package org.objectweb.proactive.examples.doctor;

import org.apache.log4j.Logger;


public class Receptionnist implements org.objectweb.proactive.RunActive {
    static Logger logger = Logger.getLogger(Receptionnist.class.getName());
    public final static int NO_ONE = -1;
    int pat_id;
    int doc_id;
    Office off;

    public Receptionnist() {
    }

    public Receptionnist(Office _off) {
        off = _off;
        doc_id = pat_id = NO_ONE;
    }

    public synchronized void addPatient(int pat) {
        if (pat_id != NO_ONE) {
            logger.error("ERROR: addPatient(" + pat + ") with pat_id=" +
                pat_id);
            System.exit(0);
        }
        pat_id = pat;
        if (doc_id != NO_ONE) {
            off.doctorWithPatient(doc_id, pat_id);
            doc_id = pat_id = NO_ONE;
        }
    }

    public synchronized void addDoctor(int doc) {
        if (doc_id != NO_ONE) {
            logger.error("ERROR: addDoctor(" + doc + ") with doc_id=" + doc_id);
            System.exit(0);
        }
        doc_id = doc;
        if (pat_id != NO_ONE) {
            off.doctorWithPatient(doc_id, pat_id);
            doc_id = pat_id = NO_ONE;
        }
    }

    public synchronized boolean doctorWaiting() {
        return ((doc_id != NO_ONE) && (pat_id == NO_ONE));
    }

    public synchronized boolean patientWaiting() {
        return ((pat_id != NO_ONE) && (doc_id == NO_ONE));
    }

    public void runActivity(org.objectweb.proactive.Body body) {
        org.objectweb.proactive.Service service = new org.objectweb.proactive.Service(body);
        while (body.isActive()) {
            if (doctorWaiting()) {
                service.blockingServeOldest("addPatient");
            }
            if (patientWaiting()) {
                service.blockingServeOldest("addDoctor");
            }
            if ((!doctorWaiting()) && (!patientWaiting())) {
                service.blockingServeOldest();
            }
        }
    }
}
