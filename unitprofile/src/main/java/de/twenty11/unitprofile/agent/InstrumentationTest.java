package de.twenty11.unitprofile.agent;

/**
 * http://blog.javabenchmark.org/2013/05/java-instrumentation-tutorial.html
 *
 */
public class InstrumentationTest {
    
    @Profile
    public void somethingElse () throws InterruptedException {
        InstrumentationTest test = new InstrumentationTest();
        test.randomSleep1();
        test.randomSleep2();
        test.randomSleep3();
    }
    
    public void randomSleep1() throws InterruptedException {
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        //System.out.printf("Sleeping1 for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);
    }
    
    public void randomSleep2() throws InterruptedException {
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        //System.out.printf("Sleeping2 for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);
    }

    public void randomSleep3() throws InterruptedException {
        long randomSleepDuration = (long) (500 + Math.random() * 700);
        //System.out.printf("Sleeping3 for %d ms ..\n", randomSleepDuration);
        Thread.sleep(randomSleepDuration);
        randomSleep2();
    }
    
    public static void main(String[] args) throws InterruptedException {
        InstrumentationTest sleeping = new InstrumentationTest();
        sleeping.somethingElse();
    }

}
