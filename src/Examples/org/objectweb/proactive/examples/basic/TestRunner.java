package org.objectweb.proactive.examples.basic;

import nonregressiontest.descriptor.defaultnodes.TestNodes;

public class TestRunner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        TestNodes testNodes = new TestNodes();
        
        try {
            testNodes.action();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
