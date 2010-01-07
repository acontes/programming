/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of
 * 						   Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2.
 *
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package functionalTests.messagerouting;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.extra.messagerouting.protocol.TypeHelper;


public class TestTypeHelper {

    @Test
    public void testIntBound() {
        Random rand = new Random();
        int[] data = new int[] { Integer.MIN_VALUE, -2, -1, 0, 1, 2, Integer.MAX_VALUE };
        byte[] buf = new byte[32];

        for (int i = 0; i < data.length; i++) {
            int retval;
            int val = data[i];
            int offset = rand.nextInt(24);

            TypeHelper.intToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToInt(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testRandomInt() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int i = 0; i < 10000000; i++) {
            int retval;
            int val = rand.nextInt();
            int offset = rand.nextInt(24);

            TypeHelper.intToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToInt(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testLongBound() {
        Random rand = new Random();
        long[] data = new long[] { Long.MIN_VALUE, Integer.MIN_VALUE, -2, -1, 0, 1, 2, Integer.MAX_VALUE,
                Long.MAX_VALUE };
        byte[] buf = new byte[32];

        for (int i = 0; i < data.length; i++) {
            long retval;
            long val = data[i];
            int offset = rand.nextInt(24);

            TypeHelper.longToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToLong(buf, offset);
            Assert.assertTrue((0L + val) == (0L + retval));
        }
    }

    @Test
    public void testRandomLong() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int i = 0; i < 10000000; i++) {
            long retval;
            long val = rand.nextLong();
            int offset = rand.nextInt(24);

            TypeHelper.longToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToLong(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testInvalidShortInt() {
        // invalid values for intToShort
        Random rand = new Random();

        int val;
        // value > 65535
        for (int i = 0; i < 100000; i++) {
            val = rand.nextInt(Integer.MAX_VALUE - 65535) + 65535;
            try {
                TypeHelper.intToShort(val);
                Assert.fail("Calling TypeHelper.intToShort(" + val +
                    ") should have generated an IllegalArgumentException.");
            } catch (IllegalArgumentException e) {
                // success!
            }
        }

        // value < 0
        for (int i = 0; i < 100000; i++) {
            val = -rand.nextInt(Integer.MAX_VALUE);
            try {
                TypeHelper.intToShort(val);
                Assert.fail("Calling TypeHelper.intToShort(" + val +
                    ") should have generated an IllegalArgumentException.");
            } catch (IllegalArgumentException e) {
                // success!
            }
        }
    }

    @Test
    public void testValidShortInt() {

        // intToShort
        for (int testVal = 0; testVal < 65535; testVal++) {
            int retval;
            retval = TypeHelper.shortToInt(TypeHelper.intToShort(testVal));
            Assert.assertEquals(testVal, retval);
        }

        // shortToInt
        for (int testVal = 0; testVal < 65535; testVal++) {
            short retval;
            // we need short representation
            short val = (short) (testVal & 0xffff);
            retval = TypeHelper.intToShort(TypeHelper.shortToInt(val));
            Assert.assertEquals(val, retval);
        }

    }

    @Test
    public void testAllShorts() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int testVal = 0; testVal < 65535; testVal++) {
            short retval;
            short val = (short) (testVal & 0xffff);
            int offset = rand.nextInt(30);

            TypeHelper.shortToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToShort(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    @Test
    public void testRandomInet4Addr() {
        Random rand = new Random();
        byte[] buf = new byte[32];

        for (int i = 0; i < 100000; i++) {
            Inet4Address retval;
            Inet4Address val = intToInet4Addr(rand.nextInt());
            int offset = rand.nextInt(28);

            TypeHelper.inetAddrToByteArray(val, buf, offset);
            retval = TypeHelper.byteArrayToInetAddr(buf, offset);

            Assert.assertEquals(val, retval);
        }
    }

    private Inet4Address intToInet4Addr(int val) {
        byte[] ipBytes = new byte[4];
        TypeHelper.intToByteArray(val, ipBytes, 0);
        try {
            return (Inet4Address) Inet4Address.getByAddress(ipBytes);
        } catch (Exception e) {
            Assert.fail("Exception occured while generating test IP addresses:" + e.getMessage());
            return null;
        }

    }

}
