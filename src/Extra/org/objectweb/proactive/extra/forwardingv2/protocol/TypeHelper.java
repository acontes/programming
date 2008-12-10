package org.objectweb.proactive.extra.forwardingv2.protocol;

/**
 * 
 * TypeHelper allows formatting a Message, that is putting it under the form of a Byte array, or recovering it from a byte Array
 *
 */
public class TypeHelper {

    /**
     * just to test
     * @param args
     */
    public static void main(String[] args) {
        long l = 2345667;
        byte[] a = new byte[8];
        longToByteArray(l, a, 0);

        System.out.println("the decrypted value of l is : " + byteArrayToLong(a, 0));

        int i = 234;
        byte[] b = new byte[8];
        longToByteArray(i, b, 0);

        System.out.println("the decrypted value of i is : " + byteArrayToLong(b, 0));
    }

    /**
     * 
     * converts the byte representation of a long into its value.
     * 
     * @param a the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented long value
     */
    public static long byteArrayToLong(byte[] a, int offset) {
        long l = 0;

        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset] & 0xFF;

        return l;
    }

    /**
     * Copies the byte representation of a long into a byte array starting at the given offset
     * 
     * @param val the long to convert
     * @param a the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void longToByteArray(long val, byte[] a, int offset) {

        a[offset + 7] |= val & 0xFF;
        val >>= 8;
        a[offset + 6] |= val & 0xFF;
        val >>= 8;
        a[offset + 5] |= val & 0xFF;
        val >>= 8;
        a[offset + 4] |= val & 0xFF;
        val >>= 8;
        a[offset + 3] |= val & 0xFF;
        val >>= 8;
        a[offset + 2] |= val & 0xFF;
        val >>= 8;
        a[offset + 1] |= val & 0xFF;
        val >>= 8;
        a[offset] |= val & 0xFF;
    }

    /**
     * 
     * converts the byte representation of an int into its value as an integer.
     * 
     * @param a the byte array in which to find the byte representation
     * @param offset the offset in the byte array at which to find the byte representation
     * @return the represented int value
     */

    public static int byteArrayToInt(byte[] a, int offset) {
        int l = 0;

        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset++] & 0xFF;
        l <<= 8;
        l |= a[offset] & 0xFF;

        return l;
    }

    /**
     * Copies the byte representation of an int into a byte array starting at the given offset
     * 
     * @param val the int to convert
     * @param a the byte array in which to copy the byte representation
     * @param offset the index of the array at which to start copying
     */
    public static void intToByteArray(int val, byte[] a, int offset) {

        a[offset + 3] |= val & 0xFF;
        val >>= 8;
        a[offset + 2] |= val & 0xFF;
        val >>= 8;
        a[offset + 1] |= val & 0xFF;
        val >>= 8;
        a[offset] |= val & 0xFF;
    }
}
