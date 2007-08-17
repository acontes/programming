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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package functionalTests.exception;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.exceptions.NonFunctionalException;
import org.objectweb.proactive.core.exceptions.manager.NFEListener;

import functionalTests.FunctionalTest;
import static junit.framework.Assert.assertTrue;

/**
 * Test exceptions
 * @author ProActiveTeam
 * @version 1.0, 25 mars 2005
 * @since ProActive 2.2
 *
 */
public class Test extends FunctionalTest {
    public Test() {
    }

    int counter = 0;

    void good() {
        counter++;
    }

    private void bad() {
        counter = 1000;
        new Exception("Exception error").printStackTrace();
    }

    public void testMechanism(Exc r) throws Exception {
        ProActive.tryWithCatch(Exception.class);
        try {
            /* voidRT() */
            r.voidRT();
            ProActive.endTryWithCatch();
            bad();
        } catch (Exception e) {
            good();
        } finally {
            ProActive.removeTryWithCatch();
        }
        /* futureRT() */
        ProActive.tryWithCatch(RuntimeException.class);
        try {
            Exc res = r.futureRT();
            good();
            res.nothing();
            bad();
            ProActive.endTryWithCatch();
        } catch (RuntimeException re) {
            good();
        } finally {
            ProActive.removeTryWithCatch();
        }

        /* voidExc() */
        ProActive.tryWithCatch(Exception.class);
        try {
            r.voidExc();
            good();
            ProActive.waitForPotentialException();
            bad();
            ProActive.endTryWithCatch();
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        } finally {
            ProActive.removeTryWithCatch();
        }

        /* futureExc() */
        ProActive.tryWithCatch(Exception.class);
        try {
            r.futureExc();
            good();
            ProActive.endTryWithCatch();
            bad();
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        } finally {
            ProActive.removeTryWithCatch();
        }

        /* futureExc() synchronous */
        try {
            r.futureExc();
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        }

        ProActive.tryWithCatch(Exception.class);
        r.futureExc();
        try {
            ProActive.tryWithCatch(Exception.class);
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        } finally {
            ProActive.removeTryWithCatch();
        }

        ProActive.tryWithCatch(Exception.class);
        try {
            r.voidExc();
            r.futureExc();
            ProActive.endTryWithCatch();
        } catch (Exception e) {
            int size = ProActive.getAllExceptions().size();
            if (size == 2) {
                good();
            } else {
                System.out.println("size: " + size);
                bad();
            }
        } finally {
            ProActive.removeTryWithCatch();
        }

        assertTrue(counter == 14);
    }

    @org.junit.Test
    public void action() throws Exception {

        /* Server */
        Exc r = (Exc) org.objectweb.proactive.ProActive.newActive(Exc.class.getName(),
                null);

        /* Client */
        /* voidRT() */
        ProActive.addNFEListenerOnAO(r,
            new NFEListener() {

                /**
                                 *
                                 */
                private static final long serialVersionUID = 2860995251769787397L;

                public boolean handleNFE(NonFunctionalException e) {
                    good();
                    return true;
                }
            });
        r.voidRT();

        /* futureRT() */
        Exc res = r.futureRT();
        try {
            res.nothing();
            bad();
        } catch (RuntimeException re) {
            good();
        }

        /* voidExc() */
        try {
            r.voidExc();
            bad();
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        }

        /* futureExc() */
        try {
            r.futureExc();
            bad();
        } catch (Exception e) {
            if (e.getMessage().startsWith("Test")) {
                good();
            } else {
                bad();
            }
        }

        testMechanism(r);
    }

    public static void main(String[] args) {
        Test test = new Test();
        try {
            test.action();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
