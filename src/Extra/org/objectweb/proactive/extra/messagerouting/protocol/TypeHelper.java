/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.protocol;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/** Helper class that offer conversion operations between various Java types
 * 		to a byte array
 *
 * Theses functions do not depend on the endianess of the machine
 * 
 * @since ProActive 4.1.0
 */
public class TypeHelper {

    private static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_MESSAGE);

    /** Converts the byte representation of a long into its value.
     * 
     * @param a the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented long value
     */
    public static long byteArrayToLong(final byte[] buf, final int offset) {
        long ret = ((long) (buf[offset + 0] & 0xFF) << 56);
        ret |= ((long) (buf[offset + 1] & 0xFF) << 48);
        ret |= ((long) (buf[offset + 2] & 0xFF) << 40);
        ret |= ((long) (buf[offset + 3] & 0xFF) << 32);
        ret |= ((long) (buf[offset + 4] & 0xFF) << 24);
        ret |= ((long) (buf[offset + 5] & 0xFF) << 16);
        ret |= ((long) (buf[offset + 6] & 0xFF) << 8);
        ret |= ((long) (buf[offset + 7] & 0xFF));
        return ret;
    }

    /** Copies the byte representation of a long into a byte array starting at the given offset
     * 
     * @param val the long to convert
     * @param a the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void longToByteArray(final long val, final byte[] buf, final int offset) {
        buf[offset + 0] = (byte) ((val >> 56));
        buf[offset + 1] = (byte) ((val >> 48));
        buf[offset + 2] = (byte) ((val >> 40));
        buf[offset + 3] = (byte) ((val >> 32));
        buf[offset + 4] = (byte) ((val >> 24));
        buf[offset + 5] = (byte) ((val >> 16));
        buf[offset + 6] = (byte) ((val >> 8));
        buf[offset + 7] = (byte) ((val) >> 0);
    }

    /** converts the byte representation of an int into its value as an integer.
     * 
     * @param buf the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented int value
     */

    public static int byteArrayToInt(final byte[] buf, final int offset) {
        int ret = (buf[offset + 0] << 24);
        ret |= (buf[offset + 1] & 0xFF) << 16;
        ret |= (buf[offset + 2] & 0xFF) << 8;
        ret |= (buf[offset + 3] & 0xFF);

        return ret;
    }

    /** Copies the byte representation of an int into a byte array starting at the given offset
     * 
     * @param val the int to convert
     * @param buf the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void intToByteArray(final int val, final byte[] buf, final int offset) {
        buf[offset + 0] = (byte) (val >>> 24);
        buf[offset + 1] = (byte) (val >>> 16);
        buf[offset + 2] = (byte) (val >>> 8);
        buf[offset + 3] = (byte) (val);
    }

    /** Copies the byte representation of an {@link Inet4Address} into a byte array starting at the given offset
     * The byte representation will be put in Network Byte Order
     *
     * @param val the {@linkInet4Address} to convert
     * @param buf the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void inetAddrToByteArray(Inet4Address val, byte[] buf, int offset) {
        byte[] ipNBO = val.getAddress();
        for (int i = 0; i < ipNBO.length; i++) {
            buf[offset + i] = ipNBO[i];
        }
    }

    /** Converts the byte representation of an {@link Inet4Address} into its object value.
     * It is assumed(but not verified!) that the address is stored in the buffer in Network Byte Order.
     *
     * @param buf the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented {@link Inet4Address} object
     */
    public static Inet4Address byteArrayToInetAddr(byte[] buf, int offset) {

        byte[] ipNBO = new byte[4];
        for (int i = 0; i < ipNBO.length; i++) {
            ipNBO[i] = buf[offset + i];
        }

        try {
            return (Inet4Address) Inet4Address.getByAddress(ipNBO);
        } catch (UnknownHostException e) {
            // only thrown when arg is not 4 bytes long. Obviously not the case here.
            ProActiveLogger
                    .logEatedException(
                            logger,
                            "Problem with Inet4Address.getByAddress() implementation - "
                                + "calling it with a 4-bytes array throws UnknownHostException, in spite of the javadoc",
                            e);
            return null;
        } catch (ClassCastException e) {
            // it is obviously expected that the result of a getByAddress call with an 4-bytes array to be an Inet4Address
            ProActiveLogger.logEatedException(logger,
                    "Problem with Inet4Address.getByAddress() implementation - "
                        + "calling it with an raw ipv4 argument does NOT return an Inet4Address", e);
            return null;
        }
    }

    /** Copies the byte representation of a short into a byte array starting at the given offset
     *
     * @param val the short to convert
     * @param buf the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void shortToByteArray(short val, byte[] buf, int offset) {
        // unfortunately shift ops are only performed at minimum int level, see the JLS
        int intVal = shortToInt(val);
        buf[offset + 0] = (byte) (intVal >>> 8);
        buf[offset + 1] = (byte) (intVal);
    }

    /** converts the byte representation of a short into its value as a short.
     *
     * @param buf the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented short value
     */
    public static short byteArrayToShort(byte[] buf, int offset) {
        // unfortunately shift ops are only performed at minimum int level, see the JLS
        int intRet = 0;
        int shr_offset = 8;
        for (int index = 0; index < 2; index++) {
            int intBuf = (int) buf[offset + index] & 0xFF;
            intRet |= intBuf << shr_offset;
            shr_offset -= 8;
        }

        return intToShort(intRet);
    }

    /**
     * Convert an integer to a short representation.
     * Precondition: 0 <= val < 65535
     * @param val the integer value to be converted to a short
     * @return the short representation
     * @throws IllegalArgumentException if the precondition is not met
     */
    public static final short intToShort(int val) {
        if (!(0 <= val && val < 65536))
            throw new IllegalArgumentException("Invalid short value: " + val);
        return (short) (val & 0xFFFF);
    }

    /** Convert a short number to an integer
     * Postcondition: 0 <= retval < 65535
     * @param val the short to be converted
     * @return the integer representations
     */
    public static final int shortToInt(short val) {
        return (int) val & 0xFFFF;
    }
}
