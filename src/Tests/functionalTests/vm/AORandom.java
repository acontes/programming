package functionalTests.vm;

import java.io.Serializable;
import java.util.Random;


/*
 * Just a class designed for tests purposes
 */
public class AORandom implements Serializable {

    // empty constructor is required by Proactive
    public AORandom() {
    }

    public int getRandom() {
        return new Random().nextInt(512);
    }

}