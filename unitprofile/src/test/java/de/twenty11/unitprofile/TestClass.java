package de.twenty11.unitprofile;

public class TestClass {

    public TestClass(int i) {
        sleep(i);
    }

    public void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {}
    }

}
