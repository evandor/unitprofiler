package de.twenty11.unitprofile.helper;

public class TestClass {

    public TestClass(int i) {
        sleep(i);
    }

    public void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {}
    }

    public String complicatedCalculation() {
        sleep(20);
        return "slept 20ms";
    }

}
