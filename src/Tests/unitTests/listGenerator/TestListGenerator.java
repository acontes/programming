package unitTests.listGenerator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;


public class TestListGenerator {
    static protected class NumberChecker {
        List<int[]> intervals;
        List<Integer> values;

        public NumberChecker() {
            intervals = new ArrayList<int[]>();
            values = new ArrayList<Integer>();
        }

        public void addExclusionInterval(int start, int end) {
            intervals.add(new int[] { start, end });
        }

        public void addExclusionValue(int val) {
            values.add(val);
        }

        public boolean check(int candidate) {
            if (values.contains(candidate)) {
                return false;
            }

            for (int[] interval : intervals) {
                if ((candidate >= interval[0]) && (candidate <= interval[1])) {
                    return false;
                }
            }

            return true;
        }
    }

    static protected class Interval {
        public int start;
        public int end;
        public int step;
        public String startStr;
        public String endStr;

        public Interval(String intervalDef) {
            Matcher matcher = subInterval.matcher(intervalDef);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("misformed interval def : " +
                    intervalDef);
            }

            startStr = matcher.group(1);
            endStr = matcher.group(2);
            start = Integer.parseInt(startStr);
            end = Integer.parseInt(endStr);
            step = (matcher.group(3) != null)
                ? Integer.parseInt(matcher.group(3)) : 1;

            if (start >= end) {
                throw new IllegalArgumentException(
                    "wrong range : start >= end in " + intervalDef);
            }
        }
    }

    protected String testPattern;
    protected static Pattern simpleInterval;
    protected static Pattern subInterval;
    protected static final String SUB_INTERVAL_SPLIT_REGEXP = " *, *";

    public TestListGenerator() {
        simpleInterval = Pattern.compile("\\[([\\d\\-,; ]+)\\]");
        subInterval = Pattern.compile("(\\d+)-(\\d+);?(\\d+)?");
    }

    @Test
    public void test() {
        //        testPattern = "node[01-10]";
        //        testPattern = "node[01-10;2]";
        //        testPattern = "node01";
        //        testPattern = "node[1,2,3]";
        //        testPattern = "node[1,2-5,03-09;3]";
        testPattern = "node[01-10]^[2-4]";

        List<String> nodeNames = generateNames(testPattern);

        for (String nodeName : nodeNames) {
            System.out.println(nodeName);
        }

        //        Assert.assertTrue(nodeNames.size() == 10);
    }

    public List<String> generateNames(String names) {
        List<String> res = null;
        Matcher matcher = simpleInterval.matcher(names);

        if (matcher.find()) {
            String root = names.substring(0, matcher.start());
            String intervalDef = matcher.group(1);
            String exclusionInterval = null;

            if (matcher.find() && (names.charAt(matcher.start() - 1) == '^')) {
                // check for an exclusion pattern
                exclusionInterval = matcher.group(1);
                System.out.println(exclusionInterval);
            }

            String[] subIntervals = intervalDef.split(SUB_INTERVAL_SPLIT_REGEXP);
            String[] subExclusionIntervals = (exclusionInterval != null)
                ? exclusionInterval.split(SUB_INTERVAL_SPLIT_REGEXP) : null;
            res = getSubNames(root, subIntervals, subExclusionIntervals);
        } else {
            res = new ArrayList<String>();
            res.add(names);
        }

        return res;
    }

    protected List<String> getSubNames(String root, String[] subIntervals,
        String[] exclusionIntervals) {
        List<String> res = new ArrayList<String>();

        NumberChecker numberChecker = null;

        if (exclusionIntervals != null) {
            numberChecker = getNumberChecker(exclusionIntervals);
        }

        for (int i = 0; i < subIntervals.length; ++i) {
            String subIntervalDef = subIntervals[i];
            if (subIntervalDef.indexOf('-') > 0) {
                generateNames(root, subIntervalDef, numberChecker, res);
            } else {
                res.add(root + subIntervalDef);
            }
        }

        return res;
    }

    public void generateNames(String root, String subIntervalDef,
        NumberChecker numberChecker, List<String> names) {
        Interval interval = new Interval(subIntervalDef);

        String paddingFormat = getPadding(interval.startStr);

        for (int n = interval.start; n <= interval.end; n += interval.step) {
            if ((numberChecker != null) && !numberChecker.check(n)) {
                continue;
            }

            String format = MessageFormat.format("{0,number," + paddingFormat +
                    "}", n);
            //            System.out.println(format);
            names.add(root + format);
        }
    }

    public String getPadding(String group) {
        if (group.charAt(0) == '0') {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < group.length(); ++i) {
                res.append('0');
            }
            return res.toString();
        }

        return "#";
    }

    public NumberChecker getNumberChecker(String[] exclusionIntervals) {
        NumberChecker res = new NumberChecker();

        for (String intervalDef : exclusionIntervals) {
            if (intervalDef.indexOf('-') > 0) {
                Interval interval = new Interval(intervalDef);
                res.addExclusionInterval(interval.start, interval.end);
            } else {
                res.addExclusionValue(Integer.parseInt(intervalDef));
            }
        }

        return res;
    }
}
