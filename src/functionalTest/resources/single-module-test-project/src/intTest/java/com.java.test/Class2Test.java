package com.java.test;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Class2Test {

    private Class1 class1 = new Class1();

    @Test
    public void coveredShouldReturn1() {
        int covered = class1.covered(true);
        assertEquals(1, covered);
    }
}
