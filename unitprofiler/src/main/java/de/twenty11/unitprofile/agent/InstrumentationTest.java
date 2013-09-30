//package de.twenty11.unitprofile.agent;
//
//import de.twenty11.unitprofile.annotations.Profile;
//
///**
// * http://blog.javabenchmark.org/2013/05/java-instrumentation-tutorial.html
// *
// */
//public class InstrumentationTest {
//    
//    public InstrumentationTest() {
//        try {
//            sleep100();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    
//    //@Profile
//    public void somethingElse () throws InterruptedException {
//        InstrumentationTest test = new InstrumentationTest();
//        test.sleep100();
//        test.sleep200();
//        test.sleep300();
//        InstrumentationTest innerTest = new InstrumentationTest();
//        innerTest.randomSleepRandom(200, 200);
//        test.sleep100();
//        System.out.println("done");
//    }
//    
//    public void sleep100() throws InterruptedException {
//        Thread.sleep(100);
//    }
//    
//    public void sleep200() throws InterruptedException {
//        Thread.sleep(200);
//    }
//
//    public void sleep300() throws InterruptedException {
//        Thread.sleep(300);
//        sleep200();
//    }
//
//    public void randomSleepRandom(int min, int maxPlus) throws InterruptedException {
//        long randomSleepDuration = (long) (min + Math.random() * maxPlus);
//        Thread.sleep(randomSleepDuration);
//    }
//
//    public static void main(String[] args) throws InterruptedException {
//        InstrumentationTest sleeping = new InstrumentationTest();
//        sleeping.somethingElse();
//        System.out.println("done");
//    }
//
//}
