package org.objectweb.proactive.extensions.structuredp2p.core.overlay.can;

import java.util.ArrayList;
import java.util.Random;

import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.coordinates.Coordinate;


/**
 * Coordinate of type lexicographic. The lexicographical order is a natural order structure of the
 * Cartesian product of two ordered sets.
 * 
 * @author Filali Imen
 */
@SuppressWarnings("serial")
public class LexicographicCoordinate extends Coordinate {

    /**
     * Constructor.
     * 
     * @param value
     */
    public LexicographicCoordinate(String value) {
        super(value);
    }

    /**
     * Compute the position of the decimal separator. The position is given by the coordinate that
     * has the shortest string length
     * 
     * @param coord1
     *            the first coordinate.
     * @param coord2
     *            the second coordinate.
     * @return the position of the decimal separator
     */
    public long getIndexOfDecimalSeparator(Coordinate coord1, Coordinate coord2) {
        return (Math.min(coord1.getValue().length(), coord2.getValue().length()));
    }

    /**
     * Returns the pairwise sum of the code points values.If the two code point lists do not have
     * the same size the remaining elements of the longest list are append at the end.
     * 
     * @param codePtsStr1
     *            unicode code point list of characters in the first string.
     * @param codePtsStr2
     *            unicode code point list of characters in the second string.
     * @return the sum of unicode code point values.
     */
    public static ArrayList<Integer> sumUnicodeCodePoints(ArrayList<Integer> codePtsStr1,
            ArrayList<Integer> codePtsStr2) {
        ArrayList<Integer> sumCodePoints = new ArrayList<Integer>();
        int minLen = Math.min(codePtsStr1.size(), codePtsStr2.size());
        int maxL = Math.max(codePtsStr1.size(), codePtsStr2.size());

        ArrayList<Integer> longest;
        ArrayList<Integer> shortest;

        if (minLen == codePtsStr1.size()) {
            longest = codePtsStr2;
            shortest = codePtsStr1;
        } else {
            longest = codePtsStr1;
            shortest = codePtsStr2;
        }

        ArrayList<Integer> tmp = new ArrayList<Integer>(maxL);
        // add 0 at the begining to ease addition
        for (int i = minLen; i < maxL; i++) {
            tmp.add(0);
        }
        tmp.addAll(shortest);
        shortest=tmp;

         System.out.println("LexicographicCoordinate.sumUnicodeCodePoints() longest " + longest );
         System.out.println("LexicographicCoordinate.sumUnicodeCodePoints() shortest " + shortest);
        // );

        // for (int i = 0; i < minLen; i++) {
        // sumCodePoints.add(codePtsStr1.get(i) + codePtsStr2.get(i));
        // }
        //
        // ArrayList<Integer> currentCodePtsStr = codePtsStr2;
        // if (codePtsStr1.size() > codePtsStr2.size()) {
        // currentCodePtsStr = codePtsStr1;
        // }
        //
        // sumCodePoints.addAll(sumCodePoints.size(), currentCodePtsStr.subList(minLen, maxL));
        //
        //       

        int carry = 0;
        for (int i = 0; i < maxL; i++) {
            // System.out.println("LexicographicCoordinate.sumUnicodeCodePoints() adding " +
            // codePtsStr1.get(i) + " "+ codePtsStr2.get(i) + " carry " + carry);
            int r = shortest.get(i) + longest.get(i) + carry;
            carry = 0;
            if (r > Math.pow(2, 16.0)) {
                carry = 1;
                r -= Math.pow(2, 16.0);

            }
            sumCodePoints.add(r);
        }

        // reverse the result because of the add
        // int size = sumCodePoints.size();
        // for(int i =0;i<(size/2);i++) {
        // int tmp = sumCodePoints.get(i);
        // sumCodePoints.set(i, sumCodePoints.get(size-1-i));
        // sumCodePoints.set(size-1-i, tmp);
        // }
        //        

        // System.out.println("LexicographicCoordinate.sumUnicodeCodePoints()x " + sumCodePoints);
        for (int i = sumCodePoints.size() - 1; i >= 0; i++) {
            if (sumCodePoints.get(i) == 0) {
                sumCodePoints.remove(i);
            } else {
                break;
            }
        }

        return (sumCodePoints);
    }

    /**
     * Returns the middle coordinate of two Lexicographic Coordinates.
     * 
     * @param coord1
     *            the first coordinate.
     * @param coord2
     *            the second coordinate.
     * @return the middle coordinate.
     */

    public Coordinate getMiddleWith(Coordinate coord2) {
        ArrayList<Integer> codePtsStr1 = LexicographicCoordinate.fromCoordinateToUnicode(this);
        System.out.println(this.printInformation(codePtsStr1));
        ArrayList<Integer> codePtsStr2 = LexicographicCoordinate.fromCoordinateToUnicode(coord2);
        System.out.println(this.printInformation(codePtsStr2));
        ArrayList<Integer> sumCodePoints = LexicographicCoordinate.sumUnicodeCodePoints(codePtsStr1,
                codePtsStr2);
        System.out.println("LexicographicCoordinate.getMiddleWith() Sum is : "+ this.printInformation(sumCodePoints));
       // System.out.println( "Sum is : "+ this.printInformation(sumCodePoints));

        // Computes the middle code point value for each character element
        ArrayList<Integer> middleChrCodePts = LexicographicCoordinate.getMiddleUnicodes(sumCodePoints);
        // System.out.println("Middle is : " + this.printInformation(middleChrCodePts));
        return (new LexicographicCoordinate(LexicographicCoordinate.fromUnicodeToString(middleChrCodePts)));
    }

    /**
     * Returns the unicode code points values for the specified coordinate. The values are reversed
     * compared to the original Coordinates
     * 
     * @param coord
     *            the lexicographic coordinate.
     * @return the reversed list of unicode code points values of characters belonging to the
     *         lexicographic coordinate.
     */
    public static ArrayList<Integer> fromCoordinateToUnicode(Coordinate coord) {
        ArrayList<Integer> codePtArray = new ArrayList<Integer>();
        for (int i = coord.getValue().length() - 1; i >= 0; i--) {
            int codePt = coord.getValue().codePointAt(i);
            codePtArray.add(codePt);
        }
        return (codePtArray);
    }

    /**
     * Returns the string value from the unicode code point values.
     * 
     * @param codePoints
     *            the middle code point arrays value.
     * @return the string value from the unicode code point values.
     */
    public static String fromUnicodeToString(ArrayList<Integer> codePoints) {
        StringBuffer buf = new StringBuffer("");
        for (int i = codePoints.size() - 1; i >= 0; i--) {
            int c = codePoints.get(i);
            buf.append((char) c);
        }
//        for (int i = 0; i < codePoints.size(); i++) {
//            int c = codePoints.get(i);
//            buf.append((char) c);
//        }
        return buf.toString();
    }

    /**
     * Returns the middle of elements in the unicode code point list. If the remainder is not
     * <code>null</code>, it will be concatenated with the next element if it exists otherwise it
     * will be appended at the end. This list will be used to retrieve the string value of the
     * middle coordinate from its code point values.
     * 
     * @param codePoints
     *            the code points values.
     * @return the unicode code points list.
     */
    public static ArrayList<Integer> getMiddleUnicodes(ArrayList<Integer> codePoints) {
        ArrayList<Integer> middleChrCodePts = new ArrayList<Integer>();
        int cp;
        int remainder;
        int quotient;
        for (int i = codePoints.size() - 1; i >= 0; i--) {
            cp = codePoints.get(i);
             System.out.println("LexicographicCoordinate.getMiddleUnicodes()  point  " + cp);
            quotient = cp / 2;
            remainder = cp % 2;
            middleChrCodePts.add(quotient);
System.out.println("LexicographicCoordinate.getMiddleUnicodes() remainder " + remainder);
            if (remainder != 0) {
                // Shift the remainder to the next element
                if (i > 0) {
                    String s = remainder + "" + codePoints.get(i - 1);
                    int newCodePoint = Integer.parseInt(s);
                    codePoints.set(i - 1, newCodePoint);
                   // codePoints.set(i+1,codePoints.get(i+1)+remainder);
                } else {
                    middleChrCodePts.add(remainder);
                }
            }
        }
        // int size = middleChrCodePts.size();
        // for(int i =0;i<(size/2);i++) {
        // int tmp = middleChrCodePts.get(i);
        // middleChrCodePts.set(i, middleChrCodePts.get(size-1-i));
        // middleChrCodePts.set(size-1-i, tmp);
        // }
        
        //swap order
        int size = middleChrCodePts.size();
        for(int i =0; i< size/2;i++) {
            int tmp = middleChrCodePts.get(i);
            middleChrCodePts.set(i, middleChrCodePts.get(size-1-i));
            middleChrCodePts.set(size-1-i,tmp);
        }
System.out.println("LexicographicCoordinate.getMiddleUnicodes() " + middleChrCodePts);
        return (middleChrCodePts);

    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Coordinate coord) {
        return super.getValue().compareTo(coord.getValue());
    }

    /**
     * Generate a random {@link LexicographicCoordinate}.
     * 
     * @param length
     *            the length of the coordinate.
     * @return the generated {@link LexicographicCoordinate}.
     */
    public static LexicographicCoordinate random(int length) {
        Random rand = new Random();
        String chars = "abcdefghijklmnopqrstuvwxy";
        StringBuffer generatedString = new StringBuffer();
        for (int x = 0; x < length; x++) {
            // Create random index
            int i = rand.nextInt(chars.length());
            generatedString.append(chars.charAt(i));
        }

        return new LexicographicCoordinate(generatedString.toString());
    }

    /**
     * 
     * @param l
     * @return
     */
    public String printInformation(ArrayList<?> l) {
        String s = "(";
        for (int i = l.size() - 1; i > 0; i--) {
            s += l.get(i) + ",";
        }
        s += l.get(0) + ")";
        return (s);
    }

    /**
     * generate a random string
     * 
     * @param length
     *            the length of the string
     * @return the generated string
     */
    public static String generate(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String StringToLookFor = "";
        for (int x = 0; x < length; x++) {
            // create random index
            int i = (int) Math.floor(Math.random() * chars.length() - 1);
            StringToLookFor += chars.charAt(i);
        }
        System.out.println(StringToLookFor);
        return StringToLookFor;
    }

    public static ArrayList<Integer> alpha1() {
        ArrayList<Integer> al = new ArrayList<Integer>();

        LexicographicCoordinate l = new LexicographicCoordinate("a");
        return LexicographicCoordinate.fromCoordinateToUnicode(l);
        //        
        // al.add(98);
        // al.add(97);
        // return al;
    }

    public static ArrayList<Integer> alpha2() {
        LexicographicCoordinate l = new LexicographicCoordinate("z");
        return LexicographicCoordinate.fromCoordinateToUnicode(l);
        
    }

    public static ArrayList<Integer> alpha3() {
        ArrayList<Integer> al = new ArrayList<Integer>();
        al.add(104);
        al.add(7);
        al.add(63);
        al.add(435);
        al.add(55047);
        al.add(6202);
        al.add(692);
        al.add(232);
        al.add(29);
        al.add(97);
        return al;
    }

    public static ArrayList<Integer> alpha4() {
        ArrayList<Integer> al2 = new ArrayList<Integer>();
        al2.add(104);
        al2.add(7);
        al2.add(63);
        al2.add(436);
        al2.add(670);
        al2.add(5894);
        al2.add(68);
        al2.add(44);
        al2.add(4);
        al2.add(30);
        return al2;
    }

    public static void test() {
        ArrayList<Integer> al = LexicographicCoordinate.alpha1();
        String min = LexicographicCoordinate.fromUnicodeToString(al);
        System.out.println("First String " + min +  " " +al );
        // System.out.println(al);
        LexicographicCoordinate cmin = new LexicographicCoordinate(min);

        ArrayList<Integer> al2 = LexicographicCoordinate.alpha2();

        String max = LexicographicCoordinate.fromUnicodeToString(al2);
        System.out.println("Second String " + max + " " + al2);
        //System.out.println(al2);
        System.out.println("Test Sum " + LexicographicCoordinate.sumUnicodeCodePoints(al, al2));

      //  System.out.println("LexicographicCoordinate.test() compare min max " + min.compareTo(max));
        LexicographicCoordinate cmax = new LexicographicCoordinate(max);
        Coordinate c = cmin.getMiddleWith(cmax);

        System.out.println("LexicographicCoordinate.test() Middle is  : " + c);

        System.out
                .println("LexicographicCoordinate.test() compare min middle " + min.compareTo(c.getValue()));
        System.out
                .println("LexicographicCoordinate.test() compare middle max " + c.getValue().compareTo(max));

//        for (int i = 0; i < Math.min(min.length(), c.getValue().length()); i++) {
//            System.out.println("LexicographicCoordinate.test()    min = " + min.charAt(i) + "  middle = " +
//                c.getValue().charAt(i) + " diff : " + (c.getValue().charAt(i) - min.charAt(i)));
//        }

        System.out.println("a".compareTo("b"));
        try {
            new Zone(new Coordinate[] { cmin }, new Coordinate[] { cmax });
        } catch (ZoneException e) {
            e.printStackTrace();
        }
        try {
            new Zone(new Coordinate[] { cmin }, new Coordinate[] { c });
        } catch (ZoneException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test many successive splits.
     * 
     * @param args
     */
    public static void main(String[] args) {
    //    LexicographicCoordinate.test();
      
        Coordinate coord1;
         Coordinate middleCoord = new LexicographicCoordinate("ax");
         Coordinate coord2 = new LexicographicCoordinate("bz");
        
         int nbOfSplit =3;
         while (nbOfSplit > 0) {
         coord1 = new LexicographicCoordinate(middleCoord.getValue());
         middleCoord = middleCoord.getMiddleWith(coord2);
         System.out.println("[" + coord1.getValue() + "," + middleCoord.getValue() + "[" + ",[" +
         middleCoord.getValue() + "," + coord2.getValue() + "[");
         nbOfSplit--;
         try {
            new Zone(new Coordinate[] { coord1 }, new Coordinate[] { middleCoord });
        } catch (ZoneException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         }
    }
}