package org.apache.karaf.devx.extensions.junit;

@KarafTest
public class TestRuntime {

    @org.junit.jupiter.api.Test
    public void MyTest() {
        System.out.println("It works 1!");
    }

    @org.junit.jupiter.api.Test
    public void MyTest2() {
        System.out.println("It works 2!");
    }

    @org.junit.jupiter.api.Test
    public void MyTest3() {
        System.out.println("It works 3!");
    }
}
